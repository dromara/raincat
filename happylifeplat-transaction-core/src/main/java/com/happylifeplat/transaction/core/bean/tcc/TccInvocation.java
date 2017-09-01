package com.happylifeplat.transaction.core.bean.tcc;

import java.io.Serializable;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  TccInvocation
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public class TccInvocation implements Serializable {
    private static final long serialVersionUID = 7722060715819141844L;
    /**
     * 事务执行器
     */
    private Class targetClazz;
    /**
     * 方法
     */
    private String method;
    /**
     * 参数值
     */
    private Object[] argumentValues;
    /**
     * 参数类型
     */
    private Class[] argumentTypes;

    public TccInvocation() {
    }

    public TccInvocation(Class targetClazz, String method, Object[] argumentValues, Class[] argumentTypes) {
        this.targetClazz = targetClazz;
        this.method = method;
        this.argumentValues = argumentValues;
        this.argumentTypes = argumentTypes;
    }

    public Class getTargetClazz() {
        return targetClazz;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getArgumentValues() {
        return argumentValues;
    }

    public Class[] getArgumentTypes() {
        return argumentTypes;
    }
}
