package com.happylifeplat.transaction.core.helper;

import com.happylifeplat.transaction.common.holder.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.annotation.Annotation;
import java.util.Map;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * SpringBeanUtils
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 11:56
 * @since JDK 1.8
 */
public class SpringBeanUtils {
    private final Log LOGGER = LogFactory.getLog(getClass());
    private ConfigurableApplicationContext cfgContext;
    /**
     * 实体对象
     */
    private static final SpringBeanUtils INSTANCE = new SpringBeanUtils();

    private SpringBeanUtils() {
        if (INSTANCE != null) {
            throw new Error("error");
        }
    }

    public static SpringBeanUtils getInstance() {
        return INSTANCE;
    }

    /**
     * 防止序列化产生对象
     *
     * @return 防止序列化
     */
    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * 获取一个Bean信息
     *
     * @param type 类型
     * @param <T>  泛型
     * @return 对象
     */
    public <T> T getBean(Class<T> type) {
        Assert.notNull(type);
        return cfgContext.getBean(type);
    }

    /**
     * 获取bean的名字
     *
     * @param type 类型
     * @return bean名字
     */
    public String getBeanName(Class type) {
        Assert.notNull(type);
        return cfgContext.getBeanNamesForType(type)[0];
    }

    /**
     * 判断一个bean是否存在Spring容器中.
     *
     * @param type 类型
     * @return 成功 true 失败 false
     */
    public boolean exitsBean(Class type) {
        Assert.notNull(type);
        return cfgContext.containsBean(type.getName());
    }

    /**
     * 动态注册一个Bean动Spring容器中
     *
     * @param beanName  名称
     * @param beanClazz 定义bean
     */
    public void registerBean(String beanName, Class beanClazz, Map<String, Object> propertys) {
        Assert.notNull(beanName);
        Assert.notNull(beanClazz);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
        if (propertys != null) {
            propertys.forEach((k, v) -> builder.addPropertyValue(k, v));
        }
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        registerBean(beanName, builder.getBeanDefinition());

    }

    public void registerBean(String beanName, Object obj) {
        Assert.notNull(beanName);
        Assert.notNull(obj);
        cfgContext.getBeanFactory().registerSingleton(beanName, obj);
    }

    /**
     * 注册Bean信息
     *
     * @param beanDefinition
     */
    public void registerBean(String beanName, BeanDefinition beanDefinition) {
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) cfgContext.getBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * 根据枚举类型获取Spring注册的Bean
     *
     * @param annotationType 枚举
     * @return
     */
    public Map<String, Object> getBeanWithAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType);
        return cfgContext.getBeansWithAnnotation(annotationType);
    }

    /**
     * 动态注册一个Bean动Spring容器中
     *
     * @param beanName  名称
     * @param beanClazz 定义bean
     */
    public void registerBean(String beanName, Class beanClazz) {
        registerBean(beanName, beanClazz, null);
    }

    public void setCfgContext(ConfigurableApplicationContext cfgContext) {
        this.cfgContext = cfgContext;
    }
}
