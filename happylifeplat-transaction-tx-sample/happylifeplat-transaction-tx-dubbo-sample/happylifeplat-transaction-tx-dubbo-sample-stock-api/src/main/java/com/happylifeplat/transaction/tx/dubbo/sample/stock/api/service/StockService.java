package com.happylifeplat.transaction.tx.dubbo.sample.stock.api.service;

import com.happylifeplat.transaction.tx.dubbo.sample.stock.api.entity.Stock;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  库存服务
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/1 15:52
 * @since JDK 1.8
 */
public interface StockService {


    void save(Stock stock);


    void updateNumber(Stock stock);


    void fail(Stock stock);

    void timeOut(Stock stock);
}
