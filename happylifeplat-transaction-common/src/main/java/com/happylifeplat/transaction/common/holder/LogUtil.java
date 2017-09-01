package com.happylifeplat.transaction.common.holder;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  日志打印封装 基于jdk1.8 lambda表达式
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/3/29 14:08
 * @since JDK 1.8
 */
public class LogUtil {

    public static final LogUtil LOG_UTIL = new LogUtil();

    private LogUtil(){

    }

    public static LogUtil getInstance() {
        return LOG_UTIL;
    }


    /**
     * debug 打印日志
     * @param logger 日志
     * @param format 日志信息
     * @param supplier   supplier接口
     */
    public static  void debug(Logger logger ,String format,Supplier<Object> supplier){
        if(logger.isDebugEnabled()){
            logger.debug(format,supplier.get());
        }
    }

    public static  void debug(Logger logger ,Supplier<Object> supplier){
        if(logger.isDebugEnabled()){
            logger.debug(Objects.toString(supplier.get()));
        }
    }


    public static void info(Logger logger,String format,Supplier<Object> supplier){
        if(logger.isInfoEnabled()){
            logger.info(format,supplier.get());
        }
    }


    public static void info(Logger logger,Supplier<Object> supplier){
        if(logger.isInfoEnabled()){
            logger.info(Objects.toString(supplier.get()));
        }
    }



    public static void error(Logger logger,String format,Supplier<Object> supplier){
        if(logger.isErrorEnabled()){
            logger.error(format,supplier.get());
        }
    }

    public static void error(Logger logger,Supplier<Object> supplier){
        if(logger.isErrorEnabled()){
            logger.error(Objects.toString(supplier.get()));
        }
    }

    public static void warn(Logger logger,String format,Supplier<Object> supplier){
        if(logger.isWarnEnabled()){
            logger.warn(format,supplier.get());
        }
    }

    public static void warn(Logger logger,Supplier<Object> supplier){
        if(logger.isWarnEnabled()){
            logger.warn(Objects.toString(supplier.get()));
        }
    }




}
