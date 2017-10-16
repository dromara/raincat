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
public enum NettyResultEnum {

    /**
     * Begin transaction status enum.
     */
    SUCCESS(0, "成功"),


    /**
     * Fail netty result enum.
     */
    FAIL(1, "失败"),


    TIME_OUT(-1,"tmManager未连接或者响应超时！"),


    ;



    private int code;

    private String desc;

    NettyResultEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    /**
     * Acquire by code netty result enum.
     *
     * @param code the code
     * @return the netty result enum
     */
    public static NettyResultEnum acquireByCode(int code) {
        Optional<NettyResultEnum> actionEnum =
                Arrays.stream(NettyResultEnum.values())
                        .filter(v -> Objects.equals(v.getCode(), code))
                        .findFirst();
        return actionEnum.orElse(NettyResultEnum.SUCCESS);

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
