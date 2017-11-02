/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.happylifeplat.transaction.admin.service.recover;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.happylifeplat.transaction.admin.helper.ConvertHelper;
import com.happylifeplat.transaction.admin.helper.PageHelper;
import com.happylifeplat.transaction.admin.page.CommonPager;
import com.happylifeplat.transaction.admin.query.RecoverTransactionQuery;
import com.happylifeplat.transaction.admin.service.RecoverTransactionService;
import com.happylifeplat.transaction.admin.vo.TransactionRecoverVO;
import com.happylifeplat.transaction.common.bean.TransactionRecover;
import com.happylifeplat.transaction.common.bean.adapter.TransactionRecoverAdapter;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.exception.TransactionIoException;
import com.happylifeplat.transaction.common.holder.DateUtils;
import com.happylifeplat.transaction.common.holder.RepositoryPathUtils;
import com.happylifeplat.transaction.common.serializer.ObjectSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Description: .</p>
 * zookeeper实现
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/19 17:08
 * @since JDK 1.8
 */
public class ZookeeperRecoverTransactionServiceImpl implements RecoverTransactionService {


    private ZooKeeper zooKeeper;

    @Autowired
    private ObjectSerializer objectSerializer;


    public ZookeeperRecoverTransactionServiceImpl(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }


    /**
     * 分页获取补偿事务信息
     *
     * @param query 查询条件
     * @return CommonPager<TransactionRecoverVO>
     */
    @Override
    public CommonPager<TransactionRecoverVO> listByPage(RecoverTransactionQuery query) {

        CommonPager<TransactionRecoverVO> voCommonPager = new CommonPager<>();
        final int currentPage = query.getPageParameter().getCurrentPage();
        final int pageSize = query.getPageParameter().getPageSize();

        int start = (currentPage - 1) * pageSize;

        final String rootPath = RepositoryPathUtils.buildZookeeperPath(query.getApplicationName());

        List<String> zNodePaths;

        List<TransactionRecoverVO> voList;

        int totalCount;

        try {
            //如果只查 重试条件的
            if (StringUtils.isBlank(query.getTxGroupId()) && Objects.nonNull(query.getRetry())) {
                zNodePaths = zooKeeper.getChildren(rootPath, false);
                final List<TransactionRecoverVO> all = findAll(zNodePaths, rootPath);
                final List<TransactionRecoverVO> collect =
                        all.stream()
                                .filter(vo -> vo.getRetriedCount() < query.getRetry())
                                .collect(Collectors.toList());
                totalCount = collect.size();
                voList = collect.stream().skip(start).limit(pageSize).collect(Collectors.toList());

            } else if (StringUtils.isNoneBlank(query.getTxGroupId()) && Objects.isNull(query.getRetry())) {
                zNodePaths = Lists.newArrayList(query.getTxGroupId());
                totalCount = zNodePaths.size();
                voList = findAll(zNodePaths, rootPath);

            } else if (StringUtils.isNoneBlank(query.getTxGroupId()) && Objects.nonNull(query.getRetry())) {
                zNodePaths = Lists.newArrayList(query.getTxGroupId());
                totalCount = zNodePaths.size();
                voList = findAll(zNodePaths, rootPath)
                        .stream()
                        .filter(vo -> vo.getRetriedCount() < query.getRetry())
                        .collect(Collectors.toList());
            } else {
                zNodePaths = zooKeeper.getChildren(rootPath, false);
                totalCount = zNodePaths.size();
                voList = findByPage(zNodePaths, rootPath, start, pageSize);
            }
            voCommonPager.setPage(PageHelper.buildPage(query.getPageParameter(), totalCount));
            voCommonPager.setDataList(voList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voCommonPager;
    }



    /**
     * 批量删除补偿事务信息
     *
     * @param ids             ids 事务id集合
     * @param applicationName 应用名称
     * @return true 成功
     */
    @Override
    public Boolean batchRemove(List<String> ids, String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }

        final String rootPath = RepositoryPathUtils.buildZookeeperPath(applicationName);
        ids.stream().map(id -> {
            try {
                final String path = buildRootPath(rootPath, id);
                byte[] content = zooKeeper.getData(path,
                        false, new Stat());
                final TransactionRecoverAdapter adapter = objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
                zooKeeper.delete(path, adapter.getVersion());
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

        }).count();

        return Boolean.TRUE;
    }


    /**
     * 更改恢复次数
     *
     * @param id              事务id
     * @param retry           恢复次数
     * @param applicationName 应用名称
     * @return true 成功
     */
    @Override
    public Boolean updateRetry(String id, Integer retry, String applicationName) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(applicationName) || Objects.isNull(retry)) {
            return Boolean.FALSE;
        }
        final String rootPath = RepositoryPathUtils.buildZookeeperPath(applicationName);
        final String path = buildRootPath(rootPath, id);
        try {
            byte[] content = zooKeeper.getData(path,
                    false, new Stat());
            final TransactionRecoverAdapter adapter = objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
            adapter.setLastTime(DateUtils.getDateYYYY());
            adapter.setRetriedCount(retry);
            zooKeeper.create(path,
                    objectSerializer.serialize(adapter),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Boolean.FALSE;
    }

    private String buildRootPath(String rootPath, String id) {
        return String.join("/", rootPath, id);
    }

    private List<TransactionRecoverVO> findAll(List<String> zNodePaths, String rootPath) {
        return zNodePaths.stream()
                .filter(StringUtils::isNoneBlank)
                .map(zNodePath -> buildByNodePath(rootPath, zNodePath)).collect(Collectors.toList());
    }

    private List<TransactionRecoverVO> findByPage(List<String> zNodePaths, String rootPath, int start, int pageSize) {
        return zNodePaths.stream().skip(start).limit(pageSize)
                .filter(StringUtils::isNoneBlank)
                .map(zNodePath -> buildByNodePath(rootPath, zNodePath)).collect(Collectors.toList());
    }


    private TransactionRecoverVO buildByNodePath(String rootPath, String zNodePath) {
        try {
            byte[] content = zooKeeper.getData(buildRootPath(rootPath, zNodePath),
                    false, new Stat());
            final TransactionRecoverAdapter adapter =
                    objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
            return ConvertHelper.buildVO(adapter);

        } catch (KeeperException | InterruptedException | TransactionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
