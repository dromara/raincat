/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.dromara.raincat.core.annotation;

import org.dromara.raincat.common.enums.PropagationEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 二阶段分布式事务注解.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TxTransaction {

    /**
     * 事务传播.
     * @return {@linkplain PropagationEnum}
     */
    PropagationEnum propagation() default PropagationEnum.PROPAGATION_REQUIRES_NEW;

    /**
     * 事务等待的最大时间 单位秒.
     *
     * @return 多少秒
     */
    int waitMaxTime() default 60;

    /**
     * 事务管理器名称
     *
     * @return 如果为空，则取默认的
     */
    String transactionManager() default "";
}
