package com.happylifeplat.transaction.tx.manager.service;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 分布式事务执行方法
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/26 11:10
 * @since JDK 1.8
 */
public interface TxTransactionExecutor {


    /**
     * 回滚整个事务组
     *
     * @param txGroupId 事务组id
     */
    void rollBack(String txGroupId);


    /**
     * 事务预提交
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean preCommit(String txGroupId);


}
