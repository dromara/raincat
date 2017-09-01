package com.happylifeplat.transaction.common.holder;

import java.util.UUID;

/**
 * 推特公司雪花算法
 *
 * @author yu.xiao @happylifeplat.com
 * @version 1.0
 * @date 2017 /3/1 11:52
 * @since JDK 1.8
 **/
public final class IdWorkerUtils {

    private final long twepoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId = 0;
    private long datacenterId = 0;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final IdWorkerUtils idWorker = new IdWorkerUtils();

    public static IdWorkerUtils getInstance() {
        return idWorker;
    }

    private IdWorkerUtils() {

    }

    private IdWorkerUtils(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    private synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock    moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public String buildPartNumber() {
        return "P" + idWorker.nextId();
    }

    public String buildSkuCode() {
        return "S" + idWorker.nextId();
    }

    public String createTaskKey() {
        return String.valueOf(UUID.randomUUID().hashCode() & 0x7fffffff);
    }


    public String createUUID() {
        return String.valueOf(UUID.randomUUID().hashCode() & 0x7fffffff);
    }


    public String createGroupId() {
        return String.valueOf(UUID.randomUUID().hashCode() & 0x7fffffff);
    }

    public long randomUUID() {
        return idWorker.nextId();
    }


    public static void main(String[] args) {

    }
}
