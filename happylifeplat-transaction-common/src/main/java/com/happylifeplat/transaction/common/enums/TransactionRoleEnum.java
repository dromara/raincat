package com.happylifeplat.transaction.common.enums;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 事务角色枚举
 *
 * @author yu.xiao @happylifeplat.com
 * @version 1.0
 * @date 2017 /5/27 16:35
 * @since JDK 1.8
 */
public enum TransactionRoleEnum {

    /**
     * Begin transaction status enum.
     */
    START(0, "发起者"),


    /**
     * Fail netty result enum.
     */
    ACTOR(1, "参与者"),




    ;



    private int code;

    private String desc;

    TransactionRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
