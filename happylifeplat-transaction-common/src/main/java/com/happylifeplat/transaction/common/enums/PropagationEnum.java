package com.happylifeplat.transaction.common.enums;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 支持的本地事务枚举
 *
 * @author yu.xiao @happylifeplat.com
 * @version 1.0
 * @date 2017 /7/11 14:36
 * @since JDK 1.8
 */
public enum PropagationEnum {

    /**
     * PropagationEnum required propagation.
     */
    PROPAGATION_REQUIRED(0),

    /**
     * PropagationEnum supports propagation.
     */
    PROPAGATION_SUPPORTS(1),

    /**
     * PropagationEnum mandatory propagation.
     */
    PROPAGATION_MANDATORY(2),

    /**
     * PropagationEnum requires new propagation.
     */
    PROPAGATION_REQUIRES_NEW(3),

    /**
     * PropagationEnum not supported propagation.
     */
    PROPAGATION_NOT_SUPPORTED(4),

    /**
     * PropagationEnum never propagation.
     */
    PROPAGATION_NEVER(5),

    /**
     * PropagationEnum nested propagation.
     */
    PROPAGATION_NESTED(6);


    private final int value;

    PropagationEnum(int value) {
        this.value = value;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public int getValue() {
        return this.value;
    }






}
