package com.happylifeplat.transaction.core.concurrent.threadlocal;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  补偿事务对象
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 15:18
 * @since JDK 1.8
 */
public class CompensationLocal {

    private static final CompensationLocal COMPENSATION_LOCAL = new CompensationLocal();

    private CompensationLocal() {

    }

    public static CompensationLocal getInstance() {
        return COMPENSATION_LOCAL;
    }


    private static final ThreadLocal<String> currentLocal = new ThreadLocal<>();


    public void setCompensationId(String compensationId) {
        currentLocal.set(compensationId);
    }

    public String getCompensationId() {
        return currentLocal.get();
    }

    public void removeCompensationId() {
        currentLocal.remove();
    }

}
