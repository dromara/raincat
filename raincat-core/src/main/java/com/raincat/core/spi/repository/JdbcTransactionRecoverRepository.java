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

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.raincat.common.bean.TransactionInvocation;
import com.raincat.common.bean.TransactionRecover;
import com.raincat.common.config.TxConfig;
import com.raincat.common.config.TxDbConfig;
import com.raincat.common.constant.CommonConstant;
import com.raincat.common.enums.CompensationCacheTypeEnum;
import com.raincat.common.enums.CompensationOperationTypeEnum;
import com.raincat.common.exception.TransactionException;
import com.raincat.common.exception.TransactionRuntimeException;
import com.raincat.common.holder.RepositoryPathUtils;
import com.raincat.common.serializer.ObjectSerializer;
import com.raincat.core.helper.SqlHelper;
import com.raincat.core.spi.TransactionRecoverRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * jdbc impl.
 *
 * @author xiaoyu
 */
public class JdbcTransactionRecoverRepository implements TransactionRecoverRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTransactionRecoverRepository.class);

    private DruidDataSource dataSource;

    private String tableName;

    private ObjectSerializer serializer;

    @Override
    public void setSerializer(final ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public int create(final TransactionRecover recover) {
        String sql = "insert into " + tableName
                + "(id,target_class,target_method,retried_count,create_time,last_time,version,group_id,task_id,invocation,complete_flag,operation)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            final TransactionInvocation transactionInvocation = recover.getTransactionInvocation();
            final String className = transactionInvocation.getTargetClazz().getName();
            final String method = transactionInvocation.getMethod();
            final byte[] serialize = serializer.serialize(transactionInvocation);
            return executeUpdate(sql, recover.getId(), className,
                    method, recover.getRetriedCount(),
                    recover.getCreateTime(), recover.getLastTime(),
                    recover.getVersion(), recover.getGroupId(),
                    recover.getTaskId(), serialize, recover.getCompleteFlag(), recover.getOperation());
        } catch (TransactionException e) {
            e.printStackTrace();
            return FAIL_ROWS;
        }
    }

    @Override
    public int remove(final String id) {
        String sql = "delete from " + tableName + " where id = ? ";
        return executeUpdate(sql, id);
    }

    @Override
    public int update(final TransactionRecover transactionRecover) throws TransactionRuntimeException {
        String sql = "update " + tableName
                + " set last_time = ?,version =version+ 1,retried_count =retried_count+1 where id = ? and version=? ";
        if (CompensationOperationTypeEnum.TASK_EXECUTE.getCode() == transactionRecover.getOperation()) {//任务完成时更新操作
            sql = "update " + tableName
                    + " set last_time = ?,complete_flag = ? where id = ?";
            executeUpdate(sql, new Date(), CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_OK, transactionRecover.getId());
            return ROWS;
        }
        int success = executeUpdate(sql, new Date(), transactionRecover.getId(), transactionRecover.getVersion());
        if (success <= 0) {
            throw new TransactionRuntimeException(UPDATE_EX);
        }
        return success;
    }

    @Override
    public TransactionRecover findById(final String id) {
        String selectSql = "select * from " + tableName + " where id=?";
        List<Map<String, Object>> list = executeQuery(selectSql);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().filter(Objects::nonNull)
                    .map(this::buildByMap).collect(Collectors.toList()).get(0);
        }
        return null;
    }

    @Override
    public List<TransactionRecover> listAll() {
        String selectSql = "select * from " + tableName;
        List<Map<String, Object>> list = executeQuery(selectSql);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().filter(Objects::nonNull)
                    .map(this::buildByMap).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<TransactionRecover> listAllByDelay(final Date date) {
        String sb = "select * from " + tableName + " where last_time <?";
        List<Map<String, Object>> list = executeQuery(sb, date);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().filter(Objects::nonNull)
                    .map(this::buildByMap).collect(Collectors.toList());
        }
        return null;
    }

    private TransactionRecover buildByMap(final Map<String, Object> map) {
        TransactionRecover recover = new TransactionRecover();
        recover.setId((String) map.get("id"));
        recover.setRetriedCount((Integer) map.get("retried_count"));
        recover.setCreateTime((Date) map.get("create_time"));
        recover.setLastTime((Date) map.get("last_time"));
        recover.setTaskId((String) map.get("task_id"));
        recover.setGroupId((String) map.get("group_id"));
        recover.setVersion((Integer) map.get("version"));
        recover.setCompleteFlag(String.valueOf(map.get("complete_flag")));
        byte[] bytes = (byte[]) map.get("invocation");
        try {
            final TransactionInvocation transactionInvocation = serializer.deSerialize(bytes, TransactionInvocation.class);
            recover.setTransactionInvocation(transactionInvocation);
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        return recover;
    }

    @Override
    public void init(final String appName, final TxConfig txConfig) {
        dataSource = new DruidDataSource();
        final TxDbConfig txDbConfig = txConfig.getTxDbConfig();
        dataSource.setUrl(txDbConfig.getUrl());
        dataSource.setDriverClassName(txDbConfig.getDriverClassName());
        dataSource.setUsername(txDbConfig.getUsername());
        dataSource.setPassword(txDbConfig.getPassword());
        dataSource.setInitialSize(txDbConfig.getInitialSize());
        dataSource.setMaxActive(txDbConfig.getMaxActive());
        dataSource.setMinIdle(txDbConfig.getMinIdle());
        dataSource.setMaxWait(txDbConfig.getMaxWait());
        dataSource.setValidationQuery(txDbConfig.getValidationQuery());
        dataSource.setTestOnBorrow(txDbConfig.getTestOnBorrow());
        dataSource.setTestOnReturn(txDbConfig.getTestOnReturn());
        dataSource.setTestWhileIdle(txDbConfig.getTestWhileIdle());
        dataSource.setPoolPreparedStatements(txDbConfig.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(txDbConfig.getMaxPoolPreparedStatementPerConnectionSize());
        this.tableName = RepositoryPathUtils.buildDbTableName(appName);
        executeUpdate(SqlHelper.buildCreateTableSql(tableName, txDbConfig.getDriverClassName()));
    }

    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.DB.getCompensationCacheType();
    }

    private int executeUpdate(final String sql, final Object... params) {
        try {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                }
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("executeUpdate->" + e.getMessage());
            throw new TransactionRuntimeException(e);
        }
    }

    private List<Map<String, Object>> executeQuery(final String sql, final Object... params) {
        List<Map<String, Object>> list = null;
        try {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                }
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> rowData = Maps.newHashMap();
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(md.getColumnName(i), rs.getObject(i));
                    }
                    list.add(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("executeQuery->" + e.getMessage());
        }
        return list;
    }
}
