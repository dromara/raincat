package com.happylifeplat.transaction.core.bean.tcc;


import com.happylifeplat.transaction.core.bean.TransactionXid;

import java.io.Serializable;



/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  TccParticipant tcc型
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public class TccParticipant implements Serializable {

    private static final long serialVersionUID = 4127729421281425247L;

    private TransactionXid xid;

    private TccInvocation confirmInvocationContext;

    private TccInvocation cancelInvocationContext;

    public TransactionXid getXid() {
        return xid;
    }

    public void setXid(TransactionXid xid) {
        this.xid = xid;
    }

    public TccInvocation getConfirmInvocationContext() {
        return confirmInvocationContext;
    }

    public void setConfirmInvocationContext(TccInvocation confirmInvocationContext) {
        this.confirmInvocationContext = confirmInvocationContext;
    }

    public TccInvocation getCancelInvocationContext() {
        return cancelInvocationContext;
    }

    public void setCancelInvocationContext(TccInvocation cancelInvocationContext) {
        this.cancelInvocationContext = cancelInvocationContext;
    }
}
