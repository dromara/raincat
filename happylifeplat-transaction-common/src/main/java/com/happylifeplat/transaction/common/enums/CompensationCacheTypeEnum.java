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
package com.happylifeplat.transaction.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xiaoyu
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
