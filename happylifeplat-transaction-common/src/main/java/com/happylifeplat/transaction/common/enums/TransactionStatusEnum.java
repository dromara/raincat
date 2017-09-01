package com.happylifeplat.transaction.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 事务状态枚举定义
 *
 * @author yu.xiao @happylifeplat.com
 * @version 1.0
 * @date 2017 /5/27 16:35
 * @since JDK 1.8
 */
public enum TransactionStatusEnum {

    /**
     * Rollback transaction status enum.
     */
    ROLLBACK(0, "回滚"),

    /**
     * Commit transaction status enum.
     */
    COMMIT(1, "已经提交"),


    /**
     * Begin transaction status enum.
     */
    BEGIN(2, "开始"),

    /**
     * Running transaction status enum.
     */
    RUNNING(3, "执行中"),

    /**
     * Failure transaction status enum.
     */
    FAILURE(4, "失败"),

    /**
     * Complete transaction status enum.
     */
    PRE_COMMIT(5, "预提交"),

    /**
     * Lock transaction status enum.
     */
    LOCK(6, "锁定");


    private int code;

    private String desc;

    TransactionStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static TransactionStatusEnum acquireByCode(int code) {
        Optional<TransactionStatusEnum> transactionStatusEnum =
                Arrays.stream(TransactionStatusEnum.values())
                        .filter(v -> Objects.equals(v.getCode(), code))
                        .findFirst();
        return transactionStatusEnum.orElse(TransactionStatusEnum.BEGIN);

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
