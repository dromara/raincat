package com.happylifeplat.transaction.tx.springcloud.sample.wechat.service;

import com.happylifeplat.transaction.tx.springcloud.sample.wechat.entity.Wechat;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 15:27
 * @since JDK 1.8
 */
public interface WechatService {


    int payment(Wechat wechat);

    void payFail();

    void payTimeOut(Wechat wechat);
}
