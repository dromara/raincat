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

package org.dromara.raincat.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * The enum Transaction role enum.
 *
 * @author xiaoyu
 */
public enum TransactionRoleEnum {

    /**
     * Start transaction role enum.
     */
    START(0, "发起者"),

    /**
     * Actor transaction role enum.
     */
    ACTOR(1, "参与者"),

    /**
     * 事务组.
     */
    GROUP(2, "事务组");

    private int code;

    private String desc;

    TransactionRoleEnum(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TransactionRoleEnum acquireByCode(final int code) {
        Optional<TransactionRoleEnum> roleEnum =
                Arrays.stream(TransactionRoleEnum.values())
                        .filter(v -> Objects.equals(v.getCode(), code))
                        .findFirst();
        return roleEnum.orElse(TransactionRoleEnum.START);

    }

    public static String acquireDescByCode(final int code) {
        return acquireByCode(code).getDesc();
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
