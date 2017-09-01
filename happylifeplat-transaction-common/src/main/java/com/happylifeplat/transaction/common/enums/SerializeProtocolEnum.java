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
