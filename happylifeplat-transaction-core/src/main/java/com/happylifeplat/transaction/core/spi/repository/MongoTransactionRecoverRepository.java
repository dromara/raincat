package com.happylifeplat.transaction.core.spi.repository;

import com.google.common.base.Splitter;
import com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.common.holder.Assert;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.core.bean.MongoTransactionRecover;
import com.happylifeplat.transaction.core.bean.TransactionInvocation;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.config.TxMongoConfig;
import com.happylifeplat.transaction.core.spi.ObjectSerializer;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * Mongo 实现
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 19:36
 * @since JDK 1.8
 */
public class MongoTransactionRecoverRepository implements TransactionRecoverRepository {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTransactionRecoverRepository.class);

    private ObjectSerializer objectSerializer;

    private MongoTemplate template;

    private String COLLECTION_NAME;


    /**
     * 创建本地事务对象
     *
     * @param transactionRecover 事务对象
     * @return rows
     */
    @Override
    public int create(TransactionRecover transactionRecover) {
        try {
            MongoTransactionRecover mongoTransactionRecover = new MongoTransactionRecover();
            mongoTransactionRecover.setTransId(transactionRecover.getId());
            mongoTransactionRecover.setCreateTime(transactionRecover.getCreateTime());
            mongoTransactionRecover.setGroupId(transactionRecover.getGroupId());
            mongoTransactionRecover.setLastTime(transactionRecover.getLastTime());
            mongoTransactionRecover.setTaskId(transactionRecover.getTaskId());
            mongoTransactionRecover.setRetriedCount(transactionRecover.getRetriedCount());
            mongoTransactionRecover.setStatus(transactionRecover.getStatus());
            final byte[] cache = objectSerializer.serialize(transactionRecover.getTransactionInvocation());
            mongoTransactionRecover.setContents(cache);
            template.save(mongoTransactionRecover, COLLECTION_NAME);
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 删除对象
     *
     * @param id 事务对象id
     * @return rows
     */
    @Override
    public int remove(String id) {
        Assert.notNull(id);
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(id));
        template.remove(query, COLLECTION_NAME);
        return 1;
    }

    /**
     * 更新数据
     *
     * @param transactionRecover 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    @Override
    public int update(TransactionRecover transactionRecover) throws TransactionRuntimeException {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(transactionRecover.getId()));
        Update update = new Update();
        update.set("lastTime", new Date());
        update.set("retriedCount", transactionRecover.getRetriedCount()+1);
        update.set("version", transactionRecover.getVersion()+1);
        final WriteResult writeResult = template.updateFirst(query, update, MongoTransactionRecover.class, COLLECTION_NAME);
        if(writeResult.getN()<=0){
           throw new  TransactionRuntimeException("更新数据异常!");
        }
        return 1;
    }


    /**
     * 根据id获取对象
     *
     * @param id 主键id
     * @return TransactionRecover
     */
    @Override
    public TransactionRecover findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(id));
        MongoTransactionRecover cache = template.findOne(query, MongoTransactionRecover.class, COLLECTION_NAME);
        return buildByCache(cache);

    }

    private TransactionRecover buildByCache(MongoTransactionRecover cache) {
        TransactionRecover recover = new TransactionRecover();
        recover.setId(cache.getTransId());
        recover.setCreateTime(cache.getCreateTime());
        recover.setGroupId(cache.getGroupId());
        recover.setTaskId(cache.getTaskId());
        recover.setLastTime(cache.getLastTime());
        recover.setRetriedCount(cache.getRetriedCount());
        recover.setVersion(cache.getVersion());
        recover.setStatus(cache.getStatus());
        final TransactionInvocation transactionInvocation;
        try {
            transactionInvocation = objectSerializer.deSerialize(cache.getContents(), TransactionInvocation.class);
            recover.setTransactionInvocation(transactionInvocation);
        } catch (TransactionException e) {
            LogUtil.error(LOGGER, "mongodb 反序列化异常:{}", e::getLocalizedMessage);
        }
        return recover;
    }

    /**
     * 获取需要提交的事务
     *
     * @return List<TransactionRecover>
     */
    @Override
    public List<TransactionRecover> listAll() {
        Query query = new Query();
        query.addCriteria(new Criteria("status")
                .in(TransactionStatusEnum.BEGIN.getCode(),
                        TransactionStatusEnum.FAILURE.getCode(),
                        TransactionStatusEnum.ROLLBACK.getCode()));
        final List<MongoTransactionRecover> mongoTransactionRecoverList =
                template.find(query, MongoTransactionRecover.class,COLLECTION_NAME);
        if (CollectionUtils.isNotEmpty(mongoTransactionRecoverList)) {
            return mongoTransactionRecoverList.stream().map(this::buildByCache).collect(Collectors.toList());
        }

        return null;
    }

    /**
     * 初始化操作
     *
     * @param modelName 模块名称
     * @param txConfig  配置信息
     */
    @Override
    public void init(String modelName, TxConfig txConfig) {
        COLLECTION_NAME = modelName;
        final TxMongoConfig txMongoConfig = txConfig.getTxMongoConfig();
        MongoClientFactoryBean clientFactoryBean = buildMongoClientFactoryBean(txMongoConfig);
        try {
            clientFactoryBean.afterPropertiesSet();
            template = new MongoTemplate(clientFactoryBean.getObject(), txMongoConfig.getMongoDbName());
            //template.setWriteConcern(WriteConcern.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成mongoClientFacotryBean
     *
     * @param config 配置信息
     * @return bean
     */
    private MongoClientFactoryBean buildMongoClientFactoryBean(TxMongoConfig config) {
        MongoClientFactoryBean clientFactoryBean = new MongoClientFactoryBean();
        MongoCredential credential = MongoCredential.createScramSha1Credential(config.getMongoUserName(),
                config.getMongoDbName(),
                config.getMongoUserPwd().toCharArray());
        clientFactoryBean.setCredentials(new MongoCredential[]{
                credential
        });
        List<String> urls = Splitter.on(",").trimResults().splitToList(config.getMongoDbUrl());
        ServerAddress[] sds = new ServerAddress[urls.size()];
        for (int i = 0; i < sds.length; i++) {
            List<String> adds = Splitter.on(":").trimResults().splitToList(urls.get(i));
            InetSocketAddress address = new InetSocketAddress(adds.get(0), Integer.parseInt(adds.get(1)));
            sds[i] = new ServerAddress(address);
        }
        clientFactoryBean.setReplicaSetSeeds(sds);
        return clientFactoryBean;
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.MONGODB.getCompensationCacheType();
    }

    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    @Override
    public void setSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }
}
