package com.happylifeplat.transaction.core.spi;


import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.config.TxConfig;

import java.util.Date;
import java.util.List;

/**
 * <p>Description: .</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  定义事务恢复资源接口
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @since JDK 1.8
 */
public interface TransactionRecoverRepository {

    /**
     * 创建本地事务对象
     * @param transactionRecover 事务对象
     * @return rows
     */
    int create(TransactionRecover transactionRecover);

    /**
     * 删除对象
     * @param id 事务对象id
     * @return rows
     */
    int remove(String id);


    /**
     * 更新数据
     * @param transactionRecover 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    int update(TransactionRecover transactionRecover) throws TransactionRuntimeException;

    /**
     * 根据id获取对象
     * @param id 主键id
     * @return TransactionRecover
     */
    TransactionRecover findById(String id);

    /**
     * 获取需要提交的事务
     * @return  List<TransactionRecover>
     */
    List<TransactionRecover> listAll();


    /**
     * 初始化操作
     * @param modelName 模块名称
     * @param txConfig 配置信息
     */
    void init(String modelName, TxConfig txConfig) throws Exception;

    /**
     * 设置scheme
     * @return scheme 命名
     */
    String getScheme();


    /**
     * 设置序列化信息
     * @param objectSerializer 序列化实现
     */
    void setSerializer(ObjectSerializer objectSerializer);
}
