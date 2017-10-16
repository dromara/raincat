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
package com.happylifeplat.transaction.tx.dubbo.sample.stock.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.dubbo.sample.stock.api.entity.Stock;
import com.happylifeplat.transaction.tx.dubbo.sample.stock.api.service.StockService;
import com.happylifeplat.transaction.tx.dubbo.sample.stock.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyu
 */
@Service("stockService")
public class StockServiceImpl implements StockService {


    private final StockMapper stockMapper;

    @Autowired
    public StockServiceImpl(StockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }


    @Override
    @TxTransaction
    public void save(Stock stock) {
        stockMapper.save(stock);
    }

    @Override
    public void updateNumber(Stock stock) {
        stockMapper.updateNumber(stock);
    }

    @Override
    @TxTransaction
    public void fail(Stock stock) {
        stockMapper.save(null);
    }

    @Override
    @TxTransaction
    public void timeOut(Stock stock) {
        //正常保存
        stockMapper.save(stock);
        try {
            //模拟超时
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
