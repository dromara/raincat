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

import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @author xiaoyu
 */
public class ChannelSender {

    /**
     * 模块netty 长连接渠道
     */
    private Channel channel;


    private String tmDomain;

    private List<TxTransactionItem> itemList;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getTmDomain() {
        return tmDomain;
    }

    public void setTmDomain(String tmDomain) {
        this.tmDomain = tmDomain;
    }

    public List<TxTransactionItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TxTransactionItem> itemList) {
        this.itemList = itemList;
    }
}
