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

package com.raincat.core.spi.repository;

import com.google.common.base.Splitter;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.raincat.common.bean.TransactionInvocation;
import com.raincat.common.bean.TransactionRecover;
import com.raincat.common.bean.adapter.MongoAdapter;
import com.raincat.common.config.TxConfig;
import com.raincat.common.config.TxMongoConfig;
import com.raincat.common.constant.CommonConstant;
import com.raincat.common.enums.CompensationCacheTypeEnum;
import com.raincat.common.enums.CompensationOperationTypeEnum;
import com.raincat.common.enums.TransactionStatusEnum;
import com.raincat.common.exception.TransactionException;
import com.raincat.common.exception.TransactionRuntimeException;
import com.raincat.common.holder.Assert;
import com.raincat.common.holder.LogUtil;
import com.raincat.common.holder.RepositoryPathUtils;
import com.raincat.common.serializer.ObjectSerializer;
import com.raincat.core.spi.TransactionRecoverRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * mongo db impl.
 * @author xiaoyu
 */
public class MongoTransactionRecoverRepository implements TransactionRecoverRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTransactionRecoverRepository.class);

    private ObjectSerializer objectSerializer;

    private MongoTemplate template;

    private String collectionName;

    @Override
    public int create(final TransactionRecover transactionRecover) {
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
            mongoAdapter.setCompleteFlag(transactionRecover.getCompleteFlag());
            mongoAdapter.setOperation(transactionRecover.getOperation());
            template.save(mongoAdapter, collectionName);
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        return ROWS;
    }

    @Override
    public int remove(final String id) {
        Assert.notNull(id);
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(id));
        template.remove(query, collectionName);
        return ROWS;
    }

    @Override
    public int update(final TransactionRecover transactionRecover) throws TransactionRuntimeException {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(transactionRecover.getId()));
        Update update = new Update();
        if (CompensationOperationTypeEnum.TASK_EXECUTE.getCode() == transactionRecover.getOperation()) {//任务完成时更新操作
            update.set("completeFlag",CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_OK);
        } else if (CompensationOperationTypeEnum.COMPENSATION.getCode() == transactionRecover.getOperation()) {
            update.set("lastTime", new Date());
            update.set("retriedCount", transactionRecover.getRetriedCount() + 1);
            update.set("version", transactionRecover.getVersion() + 1);
        }
        final WriteResult writeResult = template.updateFirst(query, update, MongoAdapter.class, collectionName);
        if (writeResult.getN() <= 0) {
            throw new TransactionRuntimeException(UPDATE_EX);
        }
        return ROWS;
    }

    @Override
    public TransactionRecover findById(final String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(id));
        MongoAdapter cache = template.findOne(query, MongoAdapter.class, collectionName);
        return buildByCache(cache);
    }

    private TransactionRecover buildByCache(final MongoAdapter cache) {
        TransactionRecover recover = new TransactionRecover();
        recover.setId(cache.getTransId());
        recover.setCreateTime(cache.getCreateTime());
        recover.setGroupId(cache.getGroupId());
        recover.setTaskId(cache.getTaskId());
        recover.setLastTime(cache.getLastTime());
        recover.setRetriedCount(cache.getRetriedCount());
        recover.setVersion(cache.getVersion());
        recover.setStatus(cache.getStatus());
        recover.setCompleteFlag(cache.getCompleteFlag());
        recover.setOperation(cache.getOperation());
        final TransactionInvocation transactionInvocation;
        try {
            transactionInvocation = objectSerializer.deSerialize(cache.getContents(), TransactionInvocation.class);
            recover.setTransactionInvocation(transactionInvocation);
        } catch (TransactionException e) {
            LogUtil.error(LOGGER, "mongodb serialize exception:{}", e::getLocalizedMessage);
        }
        return recover;
    }

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

    @Override
    public List<TransactionRecover> listAllByDelay(final Date date) {
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

    @Override
    public void init(final String appName, final TxConfig txConfig) {
        collectionName = RepositoryPathUtils.buildMongoTableName(appName);
        final TxMongoConfig txMongoConfig = txConfig.getTxMongoConfig();
        MongoClientFactoryBean clientFactoryBean = buildMongoClientFactoryBean(txMongoConfig);
        try {
            clientFactoryBean.afterPropertiesSet();
            template = new MongoTemplate(clientFactoryBean.getObject(), txMongoConfig.getMongoDbName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MongoClientFactoryBean buildMongoClientFactoryBean(final TxMongoConfig config) {
        MongoClientFactoryBean clientFactoryBean = new MongoClientFactoryBean();
        MongoCredential credential = MongoCredential.createScramSha1Credential(config.getMongoUserName(),
                config.getMongoDbName(),
                config.getMongoUserPwd().toCharArray());
        clientFactoryBean.setCredentials(new MongoCredential[]{credential});
        List<String> urls = Splitter.on(",").trimResults().splitToList(config.getMongoDbUrl());
        final ServerAddress[] serverAddresses = urls.stream().filter(Objects::nonNull)
                .map(url -> {
                    List<String> adds = Splitter.on(":").trimResults().splitToList(url);
                    return new ServerAddress(adds.get(0), Integer.valueOf(adds.get(1)));
                }).collect(Collectors.toList()).toArray(new ServerAddress[urls.size()]);

        clientFactoryBean.setReplicaSetSeeds(serverAddresses);
        return clientFactoryBean;
    }

    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.MONGODB.getCompensationCacheType();
    }

    @Override
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }
}
