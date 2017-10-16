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

public enum SerializeProtocolEnum {

    /**
     * Jdk serialize protocol enum.
     */
    JDK("jdk"),

    /**
     * Kryo serialize protocol enum.
     */
    KRYO("kryo"),

    /**
     * Hessian serialize protocol enum.
     */
    HESSIAN("hessian"),

    /**
     * Protostuff serialize protocol enum.
     */
    PROTOSTUFF("protostuff");

    private String serializeProtocol;

    SerializeProtocolEnum(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    /**
     * Acquire serialize protocol serialize protocol enum.
     *
     * @param serializeProtocol the serialize protocol
     * @return the serialize protocol enum
     */
    public static SerializeProtocolEnum acquireSerializeProtocol(String serializeProtocol) {
        Optional<SerializeProtocolEnum> serializeProtocolEnum =
                Arrays.stream(SerializeProtocolEnum.values())
                        .filter(v -> Objects.equals(v.getSerializeProtocol(), serializeProtocol))
                        .findFirst();
        return serializeProtocolEnum.orElse(SerializeProtocolEnum.KRYO);

    }

    /**
     * Gets serialize protocol.
     *
     * @return the serialize protocol
     */
    public String getSerializeProtocol() {
        return serializeProtocol;
    }

    /**
     * Sets serialize protocol.
     *
     * @param serializeProtocol the serialize protocol
     */
    public void setSerializeProtocol(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }


}
