package com.happylifeplat.transaction.tx.springcloud.sample.alipay.service;

import com.happylifeplat.transaction.tx.springcloud.sample.alipay.entity.Alipay;

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
public interface AlipayService {


    int payment(Alipay alipay);

    void payFail();

    void payTimeOut(Alipay alipay);
}
