package org.dromara.raincat.admin.service.apps.enums;

import lombok.Getter;

/**
 * The enum Accept application name enum.
 *
 * @author chaocoffee
 */
public enum AcceptApplicationNameEnum {
    /**
     * Eureka accept application name enum.
     */
    EUREKA("eureka"),
    /**
     * Property accept application name enum.
     */
    PROPERTY("property"),
    /**
     * Unaccept accept application name enum.
     */
    UNACCEPT("unaccept");

    @Getter
    private String name;

    AcceptApplicationNameEnum(String name) {
        this.name = name;
    }

    /**
     * Gets accept application name enum.
     *
     * @param name the name
     * @return the accept application name enum
     */
    public static AcceptApplicationNameEnum getAcceptApplicationNameEnum(String name) {
        for (AcceptApplicationNameEnum acceptApplicationNameEnum : AcceptApplicationNameEnum.values()) {
            if (acceptApplicationNameEnum.name.equals(name)) {
                return acceptApplicationNameEnum;
            }
        }
        return UNACCEPT;
    }
}
