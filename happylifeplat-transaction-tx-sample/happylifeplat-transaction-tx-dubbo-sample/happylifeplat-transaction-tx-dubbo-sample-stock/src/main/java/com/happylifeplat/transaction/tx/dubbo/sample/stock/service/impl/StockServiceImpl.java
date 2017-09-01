package com.happylifeplat.transaction.tx.dubbo.sample.stock.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.dubbo.sample.stock.api.entity.Stock;
import com.happylifeplat.transaction.tx.dubbo.sample.stock.api.service.StockService;
import com.happylifeplat.transaction.tx.dubbo.sample.stock.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/1 15:54
 * @since JDK 1.8
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
