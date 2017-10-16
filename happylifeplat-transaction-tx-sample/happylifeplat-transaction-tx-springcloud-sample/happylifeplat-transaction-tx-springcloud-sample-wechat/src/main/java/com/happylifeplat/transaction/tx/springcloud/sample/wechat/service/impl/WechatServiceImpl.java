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
package com.happylifeplat.transaction.tx.springcloud.sample.wechat.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.springcloud.sample.wechat.entity.Wechat;
import com.happylifeplat.transaction.tx.springcloud.sample.wechat.mapper.WechatMapper;
import com.happylifeplat.transaction.tx.springcloud.sample.wechat.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyu
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
