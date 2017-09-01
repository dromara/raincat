package com.happylifeplat.transaction.tx.manager.service.execute;

import com.happylifeplat.transaction.common.enums.TransactionRoleEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import com.happylifeplat.transaction.tx.manager.config.Address;
import com.happylifeplat.transaction.tx.manager.service.TxManagerService;
import com.happylifeplat.transaction.tx.manager.service.TxTransactionExecutor;
import com.happylifeplat.transaction.tx.manager.socket.SocketManager;
import io.netty.channel.Channel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/27 16:26
 * @since JDK 1.8
 */
public abstract class AbstractTxTransactionExecutor implements TxTransactionExecutor {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTxTransactionExecutor.class);


    protected abstract void doRollBack(String txGroupId, List<TxTransactionItem> txTransactionItems,List<TxTransactionItem> elseItems);

    protected abstract void doCommit(String txGroupId, List<TxTransactionItem> txTransactionItems,List<TxTransactionItem> elseItems);


    private TxManagerService txManagerService;

    protected void setTxManagerService(TxManagerService txManagerService) {
        this.txManagerService = txManagerService;
    }


    /**
     * 回滚整个事务组
     *
     * @param txGroupId 事务组id
     */
    @Override
    public void rollBack(String txGroupId) {
        try {
            txManagerService.updateTxTransactionItemStatus(txGroupId, txGroupId, TransactionStatusEnum.ROLLBACK.getCode());
            final List<TxTransactionItem> txTransactionItems = txManagerService.listByTxGroupId(txGroupId);
            if (CollectionUtils.isNotEmpty(txTransactionItems)) {
                final Map<Boolean, List<TxTransactionItem>> listMap = filterData(txTransactionItems);
                if (Objects.isNull(listMap)) {
                    LogUtil.info(LOGGER, "事务组id:{},提交失败！数据不完整", () -> txGroupId);
                    return;
                }
                final List<TxTransactionItem> currentItem = listMap.get(Boolean.TRUE);
                final List<TxTransactionItem> elseItems = listMap.get(Boolean.FALSE);
                doRollBack(txGroupId, currentItem,elseItems);
            }
        } finally {
            //txManagerService.removeRedisByTxGroupId(txGroupId);
        }
    }




    /**
     * 事务预提交
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    @Override
    public Boolean preCommit(String txGroupId) {
        txManagerService.updateTxTransactionItemStatus(txGroupId, txGroupId, TransactionStatusEnum.COMMIT.getCode());
        final List<TxTransactionItem> txTransactionItems = txManagerService.listByTxGroupId(txGroupId);

        final Map<Boolean, List<TxTransactionItem>> listMap = filterData(txTransactionItems);

        if (Objects.isNull(listMap)) {
            LogUtil.info(LOGGER, "事务组id:{},提交失败！数据不完整", () -> txGroupId);
            return false;
        }
        /*
             获取当前连接的channel  为什么？
            因为如果tm是集群环境，可能业务的channel对象连接到不同的tm
            那么当前的tm可没有其他业务模块的长连接信息，那么就应该做：
            1.检查当前tm的channel状态，并只提交当前渠道的命令
            2.通知 连接到其他tm的channel，执行命令
         */

        final List<TxTransactionItem> currentItem = listMap.get(Boolean.TRUE);

        final List<TxTransactionItem> elseItems = listMap.get(Boolean.FALSE);

        //检查各位channel 是否都激活，渠道状态不是回滚的
        final Boolean ok = checkChannel(currentItem);


        if (!ok) {
            doRollBack(txGroupId, currentItem,elseItems);
        } else {
            doCommit(txGroupId, currentItem,elseItems);
        }
        return true;
    }


    private Boolean checkChannel(List<TxTransactionItem> txTransactionItems) {
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            final List<TxTransactionItem> collect = txTransactionItems.stream().filter(item -> {
                Channel channel = SocketManager.getInstance().getChannelByModelName(item.getModelName());
                return Objects.nonNull(channel) && (channel.isActive() || item.getStatus() != TransactionStatusEnum.ROLLBACK.getCode());
            }).collect(Collectors.toList());
            return txTransactionItems.size() == collect.size();
        }
        return true;

    }

    private Map<Boolean, List<TxTransactionItem>> filterData(List<TxTransactionItem> txTransactionItems){
        //过滤掉发起方的数据，发起方已经回滚，不需要再通信进行回滚
        final List<TxTransactionItem> collect = txTransactionItems.stream()
                .filter(item -> item.getRole() != TransactionRoleEnum.START.getCode())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }
        return collect.stream().collect
                (Collectors.partitioningBy(item ->
                        Objects.equals(Address.getInstance().getDomain(), item.getTmDomain())));
    }


}
