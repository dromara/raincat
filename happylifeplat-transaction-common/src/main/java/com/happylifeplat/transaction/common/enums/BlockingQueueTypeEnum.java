
package com.happylifeplat.transaction.common.enums;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  线程池阻塞队列枚举
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 16:35
 * @since JDK 1.8
 */
public enum BlockingQueueTypeEnum {

    LINKED_BLOCKING_QUEUE("Linked"),
    ARRAY_BLOCKING_QUEUE("Array"),
    SYNCHRONOUS_QUEUE("SynchronousQueue");

    private String value;

    BlockingQueueTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BlockingQueueTypeEnum fromString(String value) {
        Optional<BlockingQueueTypeEnum> blockingQueueTypeEnum =
                Arrays.stream(BlockingQueueTypeEnum.values())
                        .filter(v -> Objects.equals(v.getValue(), value))
                        .findFirst();
        return blockingQueueTypeEnum.orElse(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE);
    }

    public String toString() {
        return value;
    }
}

