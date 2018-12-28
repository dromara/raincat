package org.dromara.raincat.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionManagerHelper {

    /**
     * description:
     *
     * @param transactionManager the name of transactionManager
     *
     * @return if transactionManager is blank return the primary PlatformTransactionManager
     *         otherwise return the PlatformTransactionManager by the name.
     */
    public static PlatformTransactionManager getTransactionManager(String transactionManager) {
        if (StringUtils.isBlank(transactionManager)) {
            return SpringBeanUtils.getInstance().getBean(PlatformTransactionManager.class);
        } else {
            return SpringBeanUtils.getInstance().getBean(transactionManager, PlatformTransactionManager.class);
        }
    }

}
