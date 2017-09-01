package com.happylifeplat.transaction.core.spi.repository;

import com.alibaba.druid.pool.DruidDataSource;
import com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.core.bean.TransactionInvocation;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.config.TxDbConfig;
import com.happylifeplat.transaction.core.spi.ObjectSerializer;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: .</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * jdbc实现
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/12 10:36
 * @since JDK 1.8
 */
public class JdbcTransactionRecoverRepository implements TransactionRecoverRepository {


    private Logger logger = LoggerFactory.getLogger(JdbcTransactionRecoverRepository.class);

    private DruidDataSource dataSource;


    private String tableName;

    private ObjectSerializer serializer;

    @Override
    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public int create(TransactionRecover recover) {
        String sql = "insert into " + tableName + "(id,retried_count,create_time,last_time,version,group_id,task_id,invocation)" +
                " values(?,?,?,?,?,?,?,?)";
        try {
            final byte[] serialize = serializer.serialize(recover.getTransactionInvocation());
            return executeUpdate(sql, recover.getId(), recover.getRetriedCount(), recover.getCreateTime(), recover.getLastTime(),
                    recover.getVersion(), recover.getGroupId(), recover.getTaskId(), serialize);

        } catch (TransactionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int remove(String id) {
        String sql = "delete from " + tableName + " where id = ? ";
        return executeUpdate(sql, id);
    }

    /**
     * 更新数据
     *
     * @param transactionRecover 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    @Override
    public int update(TransactionRecover transactionRecover) throws TransactionRuntimeException {

        String sql = "update " + tableName +
                " set last_time = ?,version =version+ 1,retried_count =retried_count+1 where id = ? and version=? ";
        int success = executeUpdate(sql, new Date(), transactionRecover.getId(), transactionRecover.getVersion());
        if (success <= 0) {
            throw new TransactionRuntimeException("更新异常，数据已经被更新！");
        }
        return success;
    }


    /**
     * 根据id获取对象
     *
     * @param id 主键id
     * @return TransactionRecover
     */
    @Override
    public TransactionRecover findById(String id) {
        return null;
    }

    /**
     * 获取需要提交的事务
     *
     * @return List<TransactionRecover>
     */
    @Override
    public List<TransactionRecover> listAll() {
        String selectSql = "select * from " + tableName;
        List<Map<String, Object>> list = executeQuery(selectSql);
        List<TransactionRecover> recovers = new ArrayList<>();
        for (Map<String, Object> map : list) {
            TransactionRecover recover = new TransactionRecover();

            recover.setId((String) map.get("id"));
            recover.setRetriedCount((Integer) map.get("retried_count"));
            recover.setCreateTime((Date) map.get("create_time"));
            recover.setLastTime((Date) map.get("last_time"));
            recover.setTaskId((String) map.get("task_id"));
            recover.setGroupId((String) map.get("group_id"));
            recover.setVersion((Integer) map.get("version"));
            byte[] bytes = (byte[]) map.get("invocation");
            try {
                final TransactionInvocation transactionInvocation = serializer.deSerialize(bytes, TransactionInvocation.class);
                recover.setTransactionInvocation(transactionInvocation);
            } catch (TransactionException e) {
                e.printStackTrace();
            }
            recovers.add(recover);
        }
        return recovers;
    }

    /**
     * 初始化操作
     *
     * @param modelName 模块名称
     * @param txConfig  配置信息
     */
    @Override
    public void init(String modelName, TxConfig txConfig) {
        dataSource = new DruidDataSource();
        final TxDbConfig txDbConfig = txConfig.getTxDbConfig();
        dataSource.setUrl(txDbConfig.getUrl());
        dataSource.setDriverClassName(txDbConfig.getDriverClassName());
        dataSource.setUsername(txDbConfig.getUsername());
        dataSource.setPassword(txDbConfig.getPassword());
        dataSource.setInitialSize(2);
        dataSource.setMaxActive(20);
        dataSource.setMinIdle(0);
        dataSource.setMaxWait(60000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setPoolPreparedStatements(false);
        this.tableName = "tx_transaction_" + modelName.replaceAll("-", "_");
        executeUpdate(buildCreateTableSql(txDbConfig.getDriverClassName()));
    }

    private String buildCreateTableSql(String driverClassName) {
        String createTableSql;
        String dbType = "mysql";
        if (driverClassName.contains("mysql")) {
            dbType = "mysql";
        } else if (driverClassName.contains("sqlserver")) {
            dbType = "sqlserver";
        } else if (driverClassName.contains("oracle")) {
            dbType = "oracle";
        }
        switch (dbType) {
            case "mysql": {
                createTableSql = "CREATE TABLE `" + tableName + "` (\n" +
                        "  `id` varchar(64) NOT NULL,\n" +
                        "  `retried_count` int(3) NOT NULL,\n" +
                        "  `create_time` datetime NOT NULL,\n" +
                        "  `last_time` datetime NOT NULL,\n" +
                        "  `version` int(6) NOT NULL,\n" +
                        "  `group_id` varchar(64) NOT NULL,\n" +
                        "  `task_id` varchar(64) NOT NULL,\n" +
                        "  `invocation` longblob NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ")";
                break;
            }
            case "oracle": {
                createTableSql = "CREATE TABLE `" + tableName + "` (\n" +
                        "  `id` varchar(64) NOT NULL,\n" +
                        "  `retried_count` int(3) NOT NULL,\n" +
                        "  `create_time` date NOT NULL,\n" +
                        "  `last_time` date NOT NULL,\n" +
                        "  `version` int(6) NOT NULL,\n" +
                        "  `group_id` varchar2(64) NOT NULL,\n" +
                        "  `task_id` varchar2(64) NOT NULL,\n" +
                        "  `invocation` BLOB NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ")";
                break;
            }
            case "sqlserver": {
                createTableSql = "CREATE TABLE `" + tableName + "` (\n" +
                        "  `id` varchar(64) NOT NULL,\n" +
                        "  `retried_count` int(3) NOT NULL,\n" +
                        "  `create_time` datetime NOT NULL,\n" +
                        "  `last_time` datetime NOT NULL,\n" +
                        "  `version` int(6) NOT NULL,\n" +
                        "  `group_id` nchar(64) NOT NULL,\n" +
                        "  `task_id` nchar(64) NOT NULL,\n" +
                        "  `invocation` varbinary NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ")";
                break;
            }
            default: {
                throw new RuntimeException("dbType类型不支持,目前仅支持mysql oracle sqlserver.");
            }
        }
        return createTableSql;


    }


    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.DB.getCompensationCacheType();
    }

    private int executeUpdate(String sql, Object... params) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject((i + 1), params[i]);
                }
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("executeUpdate->" + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private List<Map<String, Object>> executeQuery(String sql, Object... params) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject((i + 1), params[i]);
                }
            }
            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            logger.error("executeQuery->" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
