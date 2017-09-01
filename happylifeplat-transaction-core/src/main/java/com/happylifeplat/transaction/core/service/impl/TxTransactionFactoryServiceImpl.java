package com.happylifeplat.transaction.core.service.impl;

import com.happylifeplat.transaction.core.bean.TxTransactionInfo;
import com.happylifeplat.transaction.core.constant.Constant;
import com.happylifeplat.transaction.core.service.TxTransactionFactoryService;
import com.happylifeplat.transaction.core.service.handler.ActorTxTransactionHandler;
import com.happylifeplat.transaction.core.service.handler.InsideCompensationHandler;
import com.happylifeplat.transaction.core.service.handler.StartCompensationHandler;
import com.happylifeplat.transaction.core.service.handler.StartTxTransactionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 判断是进行start 还是running 还是补偿
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 15:51
 * @since JDK 1.8
 */
@Service
public class TxTransactionFactoryServiceImpl implements TxTransactionFactoryService {

    @Override
    public Class factoryOf(TxTransactionInfo info) throws Throwable {
        if (StringUtils.isNoneBlank(info.getCompensationId())) {
            return StartCompensationHandler.class;
        }
        if (StringUtils.isBlank(info.getTxGroupId())) {
            return StartTxTransactionHandler.class;
        } else {
            if (Objects.equals(Constant.COMPENSATE_ID, info.getTxGroupId())) {
                return InsideCompensationHandler.class;
            }
            return ActorTxTransactionHandler.class;
        }

    }
}
