package org.dromara.raincat.common.enums;

/**
 * The enum Compensation operation type enum.
 *
 * @author chaocoffee.
 */
public enum CompensationOperationTypeEnum {

    /**
     * Task execute compensation operation type enum.
     */
    TASK_EXECUTE(1, "任务更新"),

    /**
     * Compensation compensation operation type enum.
     */
    COMPENSATION(2,"补偿操作");

    private int code;

    private String desc;

    CompensationOperationTypeEnum(final int code, final String desc) {
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
    public void setCode(final int code) {
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
    public void setDesc(final String desc) {
        this.desc = desc;
    }
}
