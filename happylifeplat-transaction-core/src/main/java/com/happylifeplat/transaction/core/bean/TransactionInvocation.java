/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.happylifeplat.transaction.core.bean;

import java.io.Serializable;

/**
 * @author xiaoyu
 */
public class TransactionInvocation implements Serializable {
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

    public TransactionInvocation() {
    }

    public TransactionInvocation(Class targetClazz, String method, Object[] argumentValues, Class[] argumentTypes) {
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
