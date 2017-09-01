package com.happylifeplat.transaction.tx.manager.config;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 19:01
 * @since JDK 1.8
 */
public interface Constant {

    String applicationName = "tx-manager";

    String REDIS_PRE_FIX = "transaction:group:%s";

    String REDIS_KEYS = "transaction:group:*";

    String httpCommit = "http://%s/tx/manager/httpCommit";

    String httpRollback = "http://%s/tx/manager/httpRollback";


}
