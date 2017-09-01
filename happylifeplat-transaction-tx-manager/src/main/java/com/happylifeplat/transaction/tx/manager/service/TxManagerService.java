package com.happylifeplat.transaction.tx.manager.service;

import com.happylifeplat.transaction.common.netty.bean.TxTransactionGroup;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;

import java.util.List;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxManager 管理事务组service
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 18:19
 * @since JDK 1.8
 */
public interface TxManagerService {


    /**
     * 保存事务组 在事务发起方的时候进行调用
     *
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     */
    Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup);


    /**
     * 往事务组添加事务
     *
     * @param txGroupId         事务组id
     * @param txTransactionItem 子事务项
     * @return true 成功 false 失败
     */
    Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem);

    /**
     * 根据事务组id 获取所有的子项目
     *
     * @param txGroupId 事务组id
     * @return List<TxTransactionItem>
     */
    List<TxTransactionItem> listByTxGroupId(String txGroupId);


    /**
     * 删除事务组信息  当回滚的时候 或者事务组完全提交的时候
     *
     * @param txGroupId txGroupId 事务组id
     */
    void removeRedisByTxGroupId(String txGroupId);


    /**
     * 更新事务状态
     * @param key  redis key 也就是txGroupId
     * @param hashKey  也就是taskKey
     * @param status  事务状态
     * @return true 成功 false 失败
     */
    Boolean updateTxTransactionItemStatus(String key,String hashKey,int status);


    /**
     * 获取事务组的状态
     * @param txGroupId 事务组id
     * @return 事务组状态
     */
    int findTxTransactionGroupStatus(String txGroupId);


    /**
     * 删除已经提交的事务组 每个子项都必须提交才删除
     * @return  true 成功  false 失败
     */
    Boolean removeCommitTxGroup();


    /**
     * 删除回滚的事务组
     * @return true 成功  false 失败
     */
    Boolean removeRollBackTxGroup();
}
