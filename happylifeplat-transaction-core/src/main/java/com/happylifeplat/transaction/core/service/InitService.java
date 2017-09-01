package com.happylifeplat.transaction.core.service;

import com.happylifeplat.transaction.core.config.TxConfig;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  Tx事务初始化服务
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 17:21
 * @since JDK 1.8
 */
public interface InitService {

    void  initialization(TxConfig txConfig);
}
