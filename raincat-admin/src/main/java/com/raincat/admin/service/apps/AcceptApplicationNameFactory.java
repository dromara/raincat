package com.raincat.admin.service.apps;

import com.google.common.collect.Lists;
import com.raincat.admin.service.apps.enums.AcceptApplicationNameEnum;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户端从注册中心获取应用名称-工厂类
 *
 * @author chaoscoffee
 * @date 2018/7/6
 * @description
 */
@Component
public class AcceptApplicationNameFactory implements ApplicationContextAware {

    //@Value("${accept.apps.server.type}")
    private String acceptType;

    private static AppsType types;

    private static Map<AcceptApplicationNameEnum, AcceptApplicationNameService> beanMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, AcceptApplicationNameService> map = applicationContext.getBeansOfType(AcceptApplicationNameService.class);
        beanMap = new HashMap<>();
        map.forEach((k, v) -> beanMap.put(v.code(), v));
    }

    /**
     * 根据不同的类型获取bean
     *
     * @param code
     * @param <T>
     * @return
     */
    public <T extends AcceptApplicationNameService> T getBean(AcceptApplicationNameEnum code) {
        return (T) beanMap.get(code);
    }

    /**
     * 根据配置文件来获取bean
     *
     * @param <T>
     * @return
     */
    public <T extends AcceptApplicationNameService> T getBean(String code) {
        return getBean(AcceptApplicationNameEnum.getAcceptApplicationNameEnum(code));
    }

    /**
     * 根据单一配置文件来获取bean
     *
     * @param <T>
     * @return
     */
    public <T extends AcceptApplicationNameService> T getBean() {
        return getBean(acceptType);
    }

    /**
     * 根据配置文件来获取beans
     * accept.apps.server.type=eureka,dubbo...
     * @param
     * @return
     */
    public List getBeans0() {
        return Arrays.asList(acceptType.split(","))
                .stream()
                .distinct()
                .map(v -> getBean(v))
                .collect(Collectors.toList());
    }

    /**
     * 根据配置文件来获取beans
     *  accept.apps.server.type[0]=eureka
     *  ...
     * @param
     * @return
     */
    public List getBeans() {
        return types.getType()
                .stream()
                .distinct()
                .map(v -> getBean(v))
                .collect(Collectors.toList());
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "accept.apps.server")
    public static class AppsType {

        private List<AcceptApplicationNameEnum> type;

        @PostConstruct
        public void init() {
            types = this;
        }

        public AppsType() {
            this.type = Lists.newArrayList();
        }

        public List<AcceptApplicationNameEnum> getType() {
            return type;
        }

        public void setType(List<AcceptApplicationNameEnum> type) {
            this.type = type;
        }

    }
}
