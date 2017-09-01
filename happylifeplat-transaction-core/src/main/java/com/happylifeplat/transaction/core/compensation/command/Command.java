package com.happylifeplat.transaction.core.compensation.command;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 命令模式执行补偿接口
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 15:51
 * @since JDK 1.8
 */
@FunctionalInterface
public interface Command {

    /**
     * 执行命令接口
     *
     * @param txCompensationAction 封装命令信息
     */
    void execute(TxCompensationAction txCompensationAction);
}
