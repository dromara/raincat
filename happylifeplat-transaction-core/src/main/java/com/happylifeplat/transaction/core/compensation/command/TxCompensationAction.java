package com.happylifeplat.transaction.core.compensation.command;

import com.happylifeplat.transaction.common.enums.CompensationActionEnum;
import com.happylifeplat.transaction.core.bean.TransactionRecover;

import java.io.Serializable;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 15:52
 * @since JDK 1.8
 */
public class TxCompensationAction implements Serializable {

    private static final long serialVersionUID = 7474184793963072848L;


    private CompensationActionEnum compensationActionEnum;


    private TransactionRecover transactionRecover;

    public CompensationActionEnum getCompensationActionEnum() {
        return compensationActionEnum;
    }

    public void setCompensationActionEnum(CompensationActionEnum compensationActionEnum) {
        this.compensationActionEnum = compensationActionEnum;
    }

    public TransactionRecover getTransactionRecover() {
        return transactionRecover;
    }

    public void setTransactionRecover(TransactionRecover transactionRecover) {
        this.transactionRecover = transactionRecover;
    }


}
