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
package com.happylifeplat.transaction.tx.manager.socket;

import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xiaoyu
 */
public class SocketManager {

    /**
     * 最大连接数
     */
    private int maxConnection = 50;

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    /**
     * 当前连接数
     */

    private int nowConnection;

    /**
     * 允许连接请求 true允许 false拒绝
     */
    private volatile boolean allowConnection = true;

    private List<Channel> clients = Lists.newCopyOnWriteArrayList();

    private static SocketManager manager = new SocketManager();

    private SocketManager() {
    }

    public static SocketManager getInstance() {
        return manager;
    }


    public Channel getChannelByModelName(String name) {
        if (CollectionUtils.isNotEmpty(clients)) {
            final Optional<Channel> first = clients.stream().filter(channel ->
                    Objects.equals(channel.remoteAddress().toString(), name))
                    .findFirst();
            return first.orElse(null);
        }
        return null;
    }


    public void addClient(Channel client) {
        clients.add(client);
        nowConnection = clients.size();
        allowConnection = (maxConnection != nowConnection);
    }

    public void removeClient(Channel client) {
        clients.remove(client);
        nowConnection = clients.size();
        allowConnection = (maxConnection != nowConnection);
    }


    public int getMaxConnection() {
        return maxConnection;
    }

    public int getNowConnection() {
        return nowConnection;
    }

    public boolean isAllowConnection() {
        return allowConnection;
    }
}
