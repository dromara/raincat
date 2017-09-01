package com.happylifeplat.transaction.tx.manager.service;

import com.happylifeplat.transaction.common.entity.TxManagerServer;
import com.happylifeplat.transaction.common.entity.TxManagerServiceDTO;
import com.happylifeplat.transaction.tx.manager.entity.TxManagerInfo;

import java.util.List;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  获取TxManager 基本信息服务
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/14 17:44
 * @since JDK 1.8
 */
public interface TxManagerInfoService {

    /**
     * 业务端获取TxManager信息
     * @return TxManagerServer
     */
    TxManagerServer findTxManagerServer();


    /**
     * 服务端信息
     * @return TxManagerInfo
     */
    TxManagerInfo findTxManagerInfo();

    /**
     * 获取eureka上的注册服务
     * @return List<TxManagerServiceDTO>
     */
    List<TxManagerServiceDTO> loadTxManagerService();




}
