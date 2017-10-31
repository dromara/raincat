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
package com.happylifeplat.transaction.core.spi.repository;

import com.google.common.base.Splitter;
import com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.common.holder.Assert;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.holder.RepositoryPathUtils;
import com.happylifeplat.transaction.common.serializer.ObjectSerializer;
import com.happylifeplat.transaction.common.bean.adapter.MongoAdapter;
import com.happylifeplat.transaction.common.bean.TransactionInvocation;
import com.happylifeplat.transaction.common.bean.TransactionRecover;
import com.happylifeplat.transaction.common.config.TxConfig;
import com.happylifeplat.transaction.common.config.TxMongoConfig;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xiaoyu
 */
public class MongoTransactionRecoverRepository implements TransactionRecoverRepository {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTransactionRecoverRepository.class);

    private ObjectSerializer objectSerializer;

    private MongoTemplate template;

    private String collectionName;


    /**
     * 创建本地事务对象
     *
     * @param transactionRecover 事务对象
     * @return rows
     */
    @Override
    public int create(TransactionRecover transactionRecover) {
        try {
            MongoAdapter mongoAdapter = new MongoAdapter();
            mongoAdapter.setTransId(transactionRecover.getId());
            mongoAdapter.setCreateTime(transactionRecover.getCreateTime());
            mongoAdapter.setGroupId(transactionRecover.getGroupId());
            mongoAdapter.setLastTime(transactionRecover.getLastTime());
            mongoAdapter.setTaskId(transactionRecover.getTaskId());
            mongoAdapter.setRetriedCount(transactionRecover.getRetriedCount());
            mongoAdapter.setStatus(transactionRecover.getStatus());
            mongoAdapter.setVersion(transactionRecover.getVersion());
            final TransactionInvocation invocation = transactionRecover.getTransactionInvocation();
            mongoAdapter.setTargetClass(invocation.getTargetClazz().getName());
            mongoAdapter.setTargetMethod(invocation.getMethod());
            mongoAdapter.setContents(objectSerializer.serialize(invocation));
            template.save(mongoAdapter, collectionName);
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
        template.remove(query, collectionName);
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
        update.set("retriedCount", transactionRecover.getRetriedCount() + 1);
        update.set("version", transactionRecover.getVersion() + 1);
        final WriteResult writeResult = template.updateFirst(query, update, MongoAdapter.class, collectionName);
        if (writeResult.getN() <= 0) {
            throw new TransactionRuntimeException("更新数据异常!");
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
        MongoAdapter cache = template.findOne(query, MongoAdapter.class, collectionName);
        return buildByCache(cache);

    }

    private TransactionRecover buildByCache(MongoAdapter cache) {
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
        final List<MongoAdapter> mongoAdapterList =
                template.find(query, MongoAdapter.class, collectionName);
        if (CollectionUtils.isNotEmpty(mongoAdapterList)) {
            return mongoAdapterList.stream().map(this::buildByCache).collect(Collectors.toList());
        }

        return null;
    }

    /**
     * 获取延迟多长时间后的事务信息,只要为了防止并发的时候，刚新增的数据被执行
     *
     * @param date 延迟后的时间
     * @return List<TransactionRecover>
     */
    @Override
    public List<TransactionRecover> listAllByDelay(Date date) {
        Query query = new Query();
        query.addCriteria(new Criteria("status")
                .in(TransactionStatusEnum.BEGIN.getCode(),
                        TransactionStatusEnum.FAILURE.getCode(),
                        TransactionStatusEnum.ROLLBACK.getCode()))
        .addCriteria(Criteria.where("lastTime").lt(date));
        final List<MongoAdapter> mongoBeans =
                template.find(query, MongoAdapter.class, collectionName);
        if (CollectionUtils.isNotEmpty(mongoBeans)) {
            return mongoBeans.stream().map(this::buildByCache).collect(Collectors.toList());
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
        collectionName = RepositoryPathUtils.buildMongoTableName(modelName);
        final TxMongoConfig txMongoConfig = txConfig.getTxMongoConfig();
        MongoClientFactoryBean clientFactoryBean = buildMongoClientFactoryBean(txMongoConfig);
        try {
            clientFactoryBean.afterPropertiesSet();
            template = new MongoTemplate(clientFactoryBean.getObject(), txMongoConfig.getMongoDbName());
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
        final ServerAddress[] serverAddresses = urls.stream().filter(Objects::nonNull)
                .map(url -> {
                    List<String> adds = Splitter.on(":").trimResults().splitToList(url);
                    return new ServerAddress(adds.get(0), Integer.valueOf(adds.get(1)));
                }).collect(Collectors.toList()).toArray(new ServerAddress[urls.size()]);

        clientFactoryBean.setReplicaSetSeeds(serverAddresses);
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
