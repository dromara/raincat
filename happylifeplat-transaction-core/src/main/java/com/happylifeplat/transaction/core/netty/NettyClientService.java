package com.happylifeplat.transaction.core.netty;

import com.happylifeplat.transaction.common.netty.bean.HeartBeat;
import com.happylifeplat.transaction.core.config.TxConfig;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 19:06
 * @since JDK 1.8
 */
public interface NettyClientService {

    /**
     * 启动netty客户端
     */
    void start(TxConfig txConfig);

    /**
     * 停止服务
     */
    void stop();


    void doConnect();

    /**
     * 重启
     */
    void restart();


    /**
     * 检查状态
     *
     * @return TRUE 正常
     */
    boolean checkState();
}
