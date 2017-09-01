package com.happylifeplat.transaction.common.enums;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 15:55
 * @since JDK 1.8
 */
public enum CompensationActionEnum {

    SAVE(0,"保存"),

    DELETE(1,"删除"),

    UPDATE(2,"更新"),

    COMPENSATE(3,"补偿"),

    ;

    private int code;

    private String desc;

    CompensationActionEnum(int code,String desc){
        this.code=code;
        this.desc=desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
