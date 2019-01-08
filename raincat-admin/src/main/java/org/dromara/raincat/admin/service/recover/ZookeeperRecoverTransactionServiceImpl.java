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

package org.dromara.raincat.admin.service.recover;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.dromara.raincat.admin.helper.ConvertHelper;
import org.dromara.raincat.admin.helper.PageHelper;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.query.RecoverTransactionQuery;
import org.dromara.raincat.admin.service.RecoverTransactionService;
import org.dromara.raincat.admin.vo.TransactionRecoverVO;
import org.dromara.raincat.common.bean.adapter.TransactionRecoverAdapter;
import org.dromara.raincat.common.exception.TransactionException;
import org.dromara.raincat.common.holder.DateUtils;
import org.dromara.raincat.common.holder.RepositoryPathUtils;
import org.dromara.raincat.common.serializer.ObjectSerializer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * zookeeper impl.
 * @author xiaoyu(Myth)
 */
public class ZookeeperRecoverTransactionServiceImpl implements RecoverTransactionService {

    private final ZooKeeper zooKeeper;

    private final ObjectSerializer objectSerializer;

    public ZookeeperRecoverTransactionServiceImpl(final ZooKeeper zooKeeper, final ObjectSerializer objectSerializer) {
        this.zooKeeper = zooKeeper;
        this.objectSerializer = objectSerializer;
    }

    @Override
    public CommonPager<TransactionRecoverVO> listByPage(final RecoverTransactionQuery query) {
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

    @Override
    public Boolean batchRemove(final List<String> ids, final String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }
        final String rootPath = RepositoryPathUtils.buildZookeeperPath(applicationName);
        ids.stream().map(id -> {
            try {
                final String path = buildRootPath(rootPath, id);
                byte[] content = zooKeeper.getData(path,
                        false, new Stat());
                final TransactionRecoverAdapter adapter =
                        objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
                zooKeeper.delete(path, adapter.getVersion());
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }).count();
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateRetry(final String id, final Integer retry, final String applicationName) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(applicationName) || Objects.isNull(retry)) {
            return Boolean.FALSE;
        }
        final String rootPath = RepositoryPathUtils.buildZookeeperPath(applicationName);
        final String path = buildRootPath(rootPath, id);
        try {
            byte[] content = zooKeeper.getData(path,
                    false, new Stat());
            final TransactionRecoverAdapter adapter =
                    objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
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

    private String buildRootPath(final String rootPath, final String id) {
        return String.join("/", rootPath, id);
    }

    private List<TransactionRecoverVO> findAll(final List<String> zNodePaths, final String rootPath) {
        return zNodePaths.stream()
                .filter(StringUtils::isNoneBlank)
                .map(zNodePath -> buildByNodePath(rootPath, zNodePath))
                .collect(Collectors.toList());
    }

    private List<TransactionRecoverVO> findByPage(final List<String> zNodePaths,
                                                  final String rootPath,
                                                  final int start,
                                                  final int pageSize) {
        return zNodePaths.stream().skip(start).limit(pageSize)
                .filter(StringUtils::isNoneBlank)
                .map(zNodePath -> buildByNodePath(rootPath, zNodePath))
                .collect(Collectors.toList());
    }

    private TransactionRecoverVO buildByNodePath(final String rootPath, final String zNodePath) {
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
