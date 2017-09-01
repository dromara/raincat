package com.happylifeplat.transaction.common.netty.bean;

import java.io.Serializable;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * netty客户端与服务端数据交换对象
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 16:21
 * @since JDK 1.8
 */
public class HeartBeat implements Serializable {

    private static final long serialVersionUID = 4183978848464761529L;


    /**
     * 执行动作 {@linkplain com.happylifeplat.transaction.common.enums.NettyMessageActionEnum}
     */
    private int action;


    /**
     * 执行发送数据任务task key
     */
    private String key;


    /**
     * {@linkplain com.happylifeplat.transaction.common.enums.NettyResultEnum}
     */
    private int result;


    /**
     * 事务组信息
     */
    private TxTransactionGroup txTransactionGroup;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public TxTransactionGroup getTxTransactionGroup() {
        return txTransactionGroup;
    }

    public void setTxTransactionGroup(TxTransactionGroup txTransactionGroup) {
        this.txTransactionGroup = txTransactionGroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
