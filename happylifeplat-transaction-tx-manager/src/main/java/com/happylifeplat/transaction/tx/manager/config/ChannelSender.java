package com.happylifeplat.transaction.tx.manager.config;

import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import io.netty.channel.Channel;

import java.util.List;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  渠道发送命令者
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/8 16:21
 * @since JDK 1.8
 */
public class ChannelSender {

   /* private static  final  ChannelSender CHANNEL_SENDER = new ChannelSender();

    private ChannelSender(){

    }

    public static ChannelSender getInstance() {
        return CHANNEL_SENDER;
    }*/

    /**
     * 模块netty 长连接渠道
     */
    private  Channel channel;


    private  String tmDomain;

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
