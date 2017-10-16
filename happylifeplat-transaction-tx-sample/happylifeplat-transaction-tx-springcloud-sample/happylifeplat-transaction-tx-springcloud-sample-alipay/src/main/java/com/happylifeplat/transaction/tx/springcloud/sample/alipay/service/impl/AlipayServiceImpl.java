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
package com.happylifeplat.transaction.tx.springcloud.sample.alipay.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.entity.Alipay;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.mapper.AlipayMapper;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyu
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
