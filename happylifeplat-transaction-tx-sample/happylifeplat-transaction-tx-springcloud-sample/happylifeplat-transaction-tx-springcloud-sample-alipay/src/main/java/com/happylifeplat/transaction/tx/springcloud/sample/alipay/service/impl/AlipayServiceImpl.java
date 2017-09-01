package com.happylifeplat.transaction.tx.springcloud.sample.alipay.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.entity.Alipay;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.mapper.AlipayMapper;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.service.AlipayService;
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
public class AlipayServiceImpl implements AlipayService {


    private final AlipayMapper alipayMapper;

    @Autowired
    public AlipayServiceImpl(AlipayMapper alipayMapper) {
        this.alipayMapper = alipayMapper;
    }

    @Override
    @TxTransaction
    public int payment(Alipay alipay) {
       return alipayMapper.save(alipay);

    }

    @Override
    @TxTransaction
    public void payFail() {
        alipayMapper.save(null);
    }

    @Override
    @TxTransaction
    public void payTimeOut(Alipay alipay) {
        alipayMapper.save(alipay);
        try {
            //模拟超时
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
