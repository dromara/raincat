package com.happylifeplat.transaction.tx.springcloud.sample.wechat.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.springcloud.sample.wechat.entity.Wechat;
import com.happylifeplat.transaction.tx.springcloud.sample.wechat.mapper.WechatMapper;
import com.happylifeplat.transaction.tx.springcloud.sample.wechat.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 15:28
 * @since JDK 1.8
 */
@Service
public class WechatServiceImpl implements WechatService {

    private final WechatMapper wechatMapper;

    @Autowired
    public WechatServiceImpl(WechatMapper wechatMapper) {
        this.wechatMapper = wechatMapper;
    }

    @Override
    @TxTransaction
    public int payment(Wechat wechat) {
        return wechatMapper.save(wechat);
    }

    @Override
    @TxTransaction
    public void payFail() {
        wechatMapper.save(null);
    }

    @Override
    @TxTransaction
    public void payTimeOut(Wechat wechat) {
        wechatMapper.save(wechat);
        try {
            //模拟网络超时
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
