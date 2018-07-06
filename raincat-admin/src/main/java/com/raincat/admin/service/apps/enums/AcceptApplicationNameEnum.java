package com.raincat.admin.service.apps.enums;

import lombok.Getter;

/**
 * @author chaoshen
 * @date 2018/7/5
 * @description
 */
public enum AcceptApplicationNameEnum {
    EUREKA("eureka"),
    ZOOKEEPER("zookeeper"),
    DUBBO("dubbo"),
    UNACCEPT("no");

    @Getter
    private String name;

    AcceptApplicationNameEnum(String name) {
        this.name = name;
    }

    public static AcceptApplicationNameEnum getAcceptApplicationNameEnum(String name) {
        for (AcceptApplicationNameEnum acceptApplicationNameEnum : AcceptApplicationNameEnum.values()) {
            if (acceptApplicationNameEnum.name.equals(name)) {
                return acceptApplicationNameEnum;
            }
        }
        return UNACCEPT;
    }
}
