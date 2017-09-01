package com.happylifeplat.transaction.core.compensation;

import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.compensation.command.TxCompensationAction;
import com.happylifeplat.transaction.core.config.TxConfig;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 本地补偿的方法
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 14:06
 * @since JDK 1.8
 */
public interface TxCompensationService {

    void compensate();


    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     */
    void start(TxConfig txConfig) throws Exception;

    /**
     * 保存补偿事务信息
     *
     * @param transactionRecover 实体对象
     * @return 主键id
     */
    String save(TransactionRecover transactionRecover);


    /**
     * 删除补偿事务信息
     *
     * @param id 主键id
     * @return true成功 false 失败
     */
    boolean remove(String id);


    /**
     * 更新
     *
     * @param transactionRecover 实体对象
     */
    void update(TransactionRecover transactionRecover);

    /**
     * 提交补偿操作
     *
     * @param txCompensationAction 补偿命令
     */
    Boolean submit(TxCompensationAction txCompensationAction);
}
