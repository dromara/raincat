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
package com.happylifeplat.transaction.tx.manager.config;

import lombok.Data;

/**
 * @author xiaoyu
 */
@Data
public class NettyConfig {

    /**
     * 启动服务端口
     */
    private int port;

    /**
     * 最大线程数
     */
    private int maxThreads = Runtime.getRuntime().availableProcessors() << 2;


    /**
     * 客户端与服务端链接数
     */
    private int maxConnection = 50;

    /**
     * 序列化方式
     */
    private String serialize;

    /**
     * 与客户端通信最大延迟时间，超过该时间就会自动唤醒线程,返回失败  单位：秒）
     */
    private int delayTime;

    /**
     * 与客户端保持通讯的心跳时间（单位：秒）
     */
    private int heartTime;


}

