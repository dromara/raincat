package com.happylifeplat.transaction.common.exception;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  TransactionException
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 14:35
 * @since JDK 1.8
 */
public class TransactionIOException extends RuntimeException {

    private static final long serialVersionUID = 6508064607297986329L;

    public TransactionIOException(String message) {
        super(message);
    }

    public TransactionIOException(Throwable e) {
        super(e);
    }
}
