package com.happylifeplat.transaction.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 支持的序列化枚举
 *
 * @author yu.xiao @happylifeplat.com
 * @version 1.0
 * @date 2017 /5/27 16:35
 * @since JDK 1.8
 */
public enum CompensationCacheTypeEnum {

    /**
     * Db compensation cache type enum.
     */
    DB("db"),

    /**
     * File compensation cache type enum.
     */
    FILE("file"),

    /**
     * Redis compensation cache type enum.
     */
    REDIS("redis"),

    /**
     * Mongodb compensation cache type enum.
     */
    MONGODB("mongodb"),

    /**
     * Zookeeper compensation cache type enum.
     */
    ZOOKEEPER("zookeeper");

    private String compensationCacheType;

    CompensationCacheTypeEnum(String compensationCacheType) {
        this.compensationCacheType = compensationCacheType;
    }

    /**
     * Acquire compensation cache type compensation cache type enum.
     *
     * @param compensationCacheType the compensation cache type
     * @return the compensation cache type enum
     */
    public static CompensationCacheTypeEnum acquireCompensationCacheType(String compensationCacheType) {
        Optional<CompensationCacheTypeEnum> serializeProtocolEnum =
                Arrays.stream(CompensationCacheTypeEnum.values())
                        .filter(v -> Objects.equals(v.getCompensationCacheType(), compensationCacheType))
                        .findFirst();
        return serializeProtocolEnum.orElse(CompensationCacheTypeEnum.DB);
    }

    /**
     * Gets compensation cache type.
     *
     * @return the compensation cache type
     */
    public String getCompensationCacheType() {
        return compensationCacheType;
    }

    /**
     * Sets compensation cache type.
     *
     * @param compensationCacheType the compensation cache type
     */
    public void setCompensationCacheType(String compensationCacheType) {
        this.compensationCacheType = compensationCacheType;
    }
}
