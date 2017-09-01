package com.happylifeplat.transaction.tx.manager.netty;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  netty服务接口
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 17:27
 * @since JDK 1.8
 */
public interface NettyService {


    /**
     * 启动netty服务
     */
    void start() throws InterruptedException;


    /**
     * 关闭服务
     */
    void stop();


}
