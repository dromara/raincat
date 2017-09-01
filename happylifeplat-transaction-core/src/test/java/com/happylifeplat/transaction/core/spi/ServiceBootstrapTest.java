package com.happylifeplat.transaction.core.spi;

import com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum;
import com.happylifeplat.transaction.common.enums.SerializeProtocolEnum;
import com.happylifeplat.transaction.core.helper.SpringBeanUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/14 10:16
 * @since JDK 1.8
 */
public class ServiceBootstrapTest {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBootstrapTest.class);


    @Test
    public void loadFirst() throws Exception {
        final ObjectSerializer objectSerializer = ServiceBootstrap.loadFirst(ObjectSerializer.class);
        LOGGER.info("加载的序列化名称为：{}",objectSerializer.getClass().getName());

    }


    @Test
    public void  loadAll(){
        //spi  serialize
        final SerializeProtocolEnum serializeProtocolEnum =
                SerializeProtocolEnum.HESSIAN;
        final ServiceLoader<ObjectSerializer> objectSerializers = ServiceBootstrap.loadAll(ObjectSerializer.class);

        final Optional<ObjectSerializer> serializer = StreamSupport.stream(objectSerializers.spliterator(), false)
                .filter(objectSerializer ->
                        Objects.equals(objectSerializer.getScheme(), serializeProtocolEnum.getSerializeProtocol())).findFirst();

        serializer.ifPresent(objectSerializer -> LOGGER.info("加载的序列化名称为：{}", objectSerializer.getClass().getName()));



        //spi  RecoverRepository support
        final CompensationCacheTypeEnum compensationCacheTypeEnum =CompensationCacheTypeEnum.DB;
        final ServiceLoader<TransactionRecoverRepository> recoverRepositories = ServiceBootstrap.loadAll(TransactionRecoverRepository.class);


        final Optional<TransactionRecoverRepository> repositoryOptional = StreamSupport.stream(recoverRepositories.spliterator(), false)
                .filter(recoverRepository ->
                        Objects.equals(recoverRepository.getScheme(), compensationCacheTypeEnum.getCompensationCacheType())).findFirst();
        //将compensationCache实现注入到spring容器
        repositoryOptional.ifPresent(repository -> {
            serializer.ifPresent(repository::setSerializer);
            SpringBeanUtils.getInstance().registerBean(TransactionRecoverRepository.class.getName(), repository);
        });


    }

}