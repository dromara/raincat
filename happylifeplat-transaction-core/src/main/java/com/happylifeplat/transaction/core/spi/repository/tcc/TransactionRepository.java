package com.happylifeplat.transaction.core.spi.repository.tcc;

import com.happylifeplat.transaction.core.bean.TransactionXid;
import com.happylifeplat.transaction.core.bean.tcc.TccTransaction;

import java.util.Date;
import java.util.List;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  操作事务信息接口
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public interface TransactionRepository {

    /**
     * 创建事务信息
     * @param tccTransaction 事务对象
     * @return  1
     */
    int create(TccTransaction tccTransaction);

    /**
     * 更新事务信息
     * @param tccTransaction 事务对象
     * @return 1
     */
    int update(TccTransaction tccTransaction);

    /**
     * 删除事务对象
     * @param tccTransaction  事务对象
     * @return 1
     */
    int delete(TccTransaction tccTransaction);

    /**
     * 根据事务id获取对象
     * @param transactionXid 事务唯一id
     * @return TccTransaction
     */
    TccTransaction findByXid(TransactionXid transactionXid);

    List<TccTransaction> findAllUnmodifiedSince(Date date);
}
