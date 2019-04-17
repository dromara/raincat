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

package org.dromara.raincat.core.spi.repository;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.dromara.raincat.annotation.RaincatSPI;
import org.dromara.raincat.common.bean.TransactionRecover;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.config.TxZookeeperConfig;
import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.common.enums.CompensationOperationTypeEnum;
import org.dromara.raincat.common.exception.TransactionException;
import org.dromara.raincat.common.exception.TransactionIoException;
import org.dromara.raincat.common.exception.TransactionRuntimeException;
import org.dromara.raincat.common.holder.CollectionUtils;
import org.dromara.raincat.common.holder.LogUtil;
import org.dromara.raincat.common.holder.RepositoryPathUtils;
import org.dromara.raincat.common.holder.TransactionRecoverUtils;
import org.dromara.raincat.common.serializer.ObjectSerializer;
import org.dromara.raincat.core.spi.TransactionRecoverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * zookeeper impl.
 *
 * @author xiaoyu
 */
@RaincatSPI("zookeeper")
public class ZookeeperTransactionRecoverRepository implements TransactionRecoverRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperTransactionRecoverRepository.class);

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private static volatile ZooKeeper zooKeeper;

    private ObjectSerializer objectSerializer;

    private String rootPath = "/tx";

    private void setRootPath(final String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public int create(final TransactionRecover transactionRecover) {
        try {
            zooKeeper.create(getRootPath(transactionRecover.getId()),
                    TransactionRecoverUtils.convert(transactionRecover, objectSerializer),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return ROWS;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public int remove(final String id) {
        try {
            final TransactionRecover byId = findById(id);
            zooKeeper.delete(getRootPath(id), byId.getVersion() - 1);
            return ROWS;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public int update(final TransactionRecover transactionRecover) throws TransactionRuntimeException {
        try {
            if (CompensationOperationTypeEnum.TASK_EXECUTE.getCode()
                    == transactionRecover.getOperation()) {
                TransactionRecover recover = findById(transactionRecover.getId());
                recover.setCompleteFlag(CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_OK);
                zooKeeper.setData(getRootPath(recover.getId()),
                        TransactionRecoverUtils.convert(recover, objectSerializer),
                        recover.getVersion() - 2);
                return ROWS;
            }
            transactionRecover.setLastTime(new Date());
            transactionRecover.setVersion(transactionRecover.getVersion() + 1);
            transactionRecover.setRetriedCount(transactionRecover.getRetriedCount() + 1);
            zooKeeper.setData(getRootPath(transactionRecover.getId()),
                    TransactionRecoverUtils.convert(transactionRecover, objectSerializer),
                    transactionRecover.getVersion() - 2);
            return ROWS;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public TransactionRecover findById(final String id) {
        try {
            Stat stat = new Stat();
            byte[] contents = zooKeeper.getData(getRootPath(id), false, stat);
            return TransactionRecoverUtils.transformBean(contents, objectSerializer);
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

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
                            return TransactionRecoverUtils.transformBean(contents, objectSerializer);
                        } catch (KeeperException | InterruptedException | TransactionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList());
        }
        return transactionRecovers;
    }

    @Override
    public List<TransactionRecover> listAllByDelay(final Date date) {
        final List<TransactionRecover> tccTransactions = listAll();
        return tccTransactions.stream()
                .filter(transactionRecover -> transactionRecover.getLastTime().compareTo(date) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public void init(final String appName, final TxConfig txConfig) {
        setRootPath(RepositoryPathUtils.buildZookeeperPath(appName));
        try {
            connect(txConfig.getTxZookeeperConfig());
        } catch (Exception e) {
            LogUtil.error(LOGGER, "zookeeper init exception please check you config:{}", e::getMessage);
            throw new TransactionRuntimeException(e.getMessage());
        }
    }

    private void connect(final TxZookeeperConfig config) {
        try {
            zooKeeper = new ZooKeeper(config.getHost(), config.getSessionTimeOut(), watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
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

    @Override
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private String getRootPath(final String id) {
        return String.join("/", rootPath, id);
    }

}
