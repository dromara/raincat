package com.happylifeplat.transaction.core.annotation;

import com.happylifeplat.transaction.common.enums.PropagationEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * tcc型分布式事务注解
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 14:33
 * @since JDK 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Compensable {

    PropagationEnum propagation() default PropagationEnum.PROPAGATION_REQUIRED;

    String confirmMethod() default "";

    String cancelMethod() default "";


}