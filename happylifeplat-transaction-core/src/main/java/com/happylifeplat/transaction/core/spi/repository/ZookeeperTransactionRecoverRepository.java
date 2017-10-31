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
package com.happylifeplat.transaction.core.spi.repository;

import com.google.common.collect.Lists;
import com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.exception.TransactionIoException;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.holder.RepositoryPathUtils;
import com.happylifeplat.transaction.common.holder.TransactionRecoverUtils;
import com.happylifeplat.transaction.common.serializer.ObjectSerializer;
import com.happylifeplat.transaction.common.bean.TransactionRecover;
import com.happylifeplat.transaction.common.config.TxConfig;
import com.happylifeplat.transaction.common.config.TxZookeeperConfig;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


/**
 * @author xiaoyu
 */
public class ZookeeperTransactionRecoverRepository implements TransactionRecoverRepository {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperTransactionRecoverRepository.class);

    private ObjectSerializer objectSerializer;

    private String rootPath = "/tx";

    private static volatile ZooKeeper zooKeeper;

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);


    private void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * 创建本地事务对象
     *
     * @param transactionRecover 事务对象
     * @return rows
     */

    @Override
    public int create(TransactionRecover transactionRecover) {
        try {
            zooKeeper.create(getRootPath(transactionRecover.getId()),
                    TransactionRecoverUtils.convert(transactionRecover, objectSerializer),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return 1;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    /**
     * 删除对象
     *
     * @param id 事务对象id
     * @return rows
     */
    @Override
    public int remove(String id) {
        try {
            final TransactionRecover byId = findById(id);
            zooKeeper.delete(getRootPath(id), byId.getVersion() - 1);
            return 1;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    /**
     * 更新数据
     *
     * @param transactionRecover 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    @Override
    public int update(TransactionRecover transactionRecover) throws TransactionRuntimeException {
        try {
            transactionRecover.setLastTime(new Date());
            transactionRecover.setVersion(transactionRecover.getVersion() + 1);
            transactionRecover.setRetriedCount(transactionRecover.getRetriedCount() + 1);
            zooKeeper.setData(getRootPath(transactionRecover.getId()),
                    TransactionRecoverUtils.convert(transactionRecover, objectSerializer),
                    transactionRecover.getVersion() - 2);
            return 1;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    /**
     * 根据id获取对象
     *
     * @param id 主键id
     * @return TransactionRecover
     */
    @Override
    public TransactionRecover findById(String id) {
        try {
            Stat stat = new Stat();
            byte[] contents = zooKeeper.getData(getRootPath(id), false, stat);
            return TransactionRecoverUtils.transformBean(contents, objectSerializer);
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    /**
     * 获取需要提交的事务
     *
     * @return List<TransactionRecover>
     */
    @Override
    public List<TransactionRecover> listAll() {
        List<TransactionRecover> transactionRecovers = Lists.newArrayList();

        List<String> zNodePaths;
        try {
            zNodePaths = zooKeeper.getChildren(rootPath, false);
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
        if (CollectionUtils.isNotEmpty(zNodePaths)) {
            transactionRecovers = zNodePaths.stream()
                    .filter(StringUtils::isNoneBlank)
                    .map(zNodePath -> {
                        try {
                            byte[] contents = zooKeeper.getData(getRootPath(zNodePath), false, new Stat());
                            return  TransactionRecoverUtils.transformBean(contents, objectSerializer);
                        } catch (KeeperException | InterruptedException | TransactionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList());
        }

        return transactionRecovers;
    }

    /**
     * 获取延迟多长时间后的事务信息,只要为了防止并发的时候，刚新增的数据被执行
     *
     * @param date 延迟后的时间
     * @return List<TransactionRecover>
     */
    @Override
    public List<TransactionRecover> listAllByDelay(Date date) {
        final List<TransactionRecover> tccTransactions = listAll();
        return tccTransactions.stream().filter(transactionRecover -> transactionRecover.getLastTime().compareTo(date) > 0).collect(Collectors.toList());
    }

    /**
     * 初始化操作
     *
     * @param modelName 模块名称
     * @param txConfig  配置信息
     */
    @Override
    public void init(String modelName, TxConfig txConfig) {
        setRootPath(RepositoryPathUtils.buildZookeeperPath(modelName));
        try {
            connect(txConfig.getTxZookeeperConfig());
        } catch (Exception e) {
            LogUtil.error(LOGGER, "zookeeper连接异常请检查配置信息是否正确:{}", e::getMessage);
            throw new TransactionRuntimeException(e.getMessage());
        }

    }

    private void connect(TxZookeeperConfig config) {
        try {
            zooKeeper = new ZooKeeper(config.getHost(), config.getSessionTimeOut(), watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    // 放开闸门, wait在connect方法上的线程将被唤醒
                    COUNT_DOWN_LATCH.countDown();
                }
            });
            COUNT_DOWN_LATCH.await();
            Stat stat = zooKeeper.exists(rootPath, false);
            if (stat == null) {
                zooKeeper.create(rootPath, rootPath.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }


    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.ZOOKEEPER.getCompensationCacheType();
    }

    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    @Override
    public void setSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private String getRootPath(String id) {
        return String.join("/", rootPath, id);
    }


}
