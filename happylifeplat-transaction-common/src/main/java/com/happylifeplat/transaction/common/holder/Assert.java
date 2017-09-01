package com.happylifeplat.transaction.common.holder;


import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 11:56
 * @since JDK 1.8
 */
public class Assert {

    private Assert() {

    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new TransactionRuntimeException(message);
        }
    }

    public static void notNull(Object obj) {
        if (obj == null) {
            throw new TransactionRuntimeException("argument invalid,Please check");
        }
    }

    public static void checkConditionArgument(boolean condition, String message) {
        if (!condition) {
            throw new TransactionRuntimeException(message);
        }
    }

}
