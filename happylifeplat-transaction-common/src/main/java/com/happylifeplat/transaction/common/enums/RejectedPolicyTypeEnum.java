/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.happylifeplat.transaction.common.enums;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 线程池拒绝策略枚举
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 16:35
 * @since JDK 1.8
 */
public enum RejectedPolicyTypeEnum {
    ABORT_POLICY("Abort"),
    BLOCKING_POLICY("Blocking"),
    CALLER_RUNS_POLICY("CallerRuns"),
    DISCARDED_POLICY("Discarded"),
    REJECTED_POLICY("Rejected");

    private String value;

    RejectedPolicyTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RejectedPolicyTypeEnum fromString(String value) {
        Optional<RejectedPolicyTypeEnum> rejectedPolicyTypeEnum =
                Arrays.stream(RejectedPolicyTypeEnum.values())
                        .filter(v -> Objects.equals(v.getValue(), value))
                        .findFirst();
        return rejectedPolicyTypeEnum.orElse(RejectedPolicyTypeEnum.ABORT_POLICY);
    }

    public String toString() {
        return value;
    }
}

