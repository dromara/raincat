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

package com.happylifeplat.transaction.admin.spi;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Splitter;
import com.happylifeplat.transaction.admin.service.RecoverTransactionService;
import com.happylifeplat.transaction.admin.service.recover.FileRecoverTransactionServiceImpl;
import com.happylifeplat.transaction.admin.service.recover.JdbcRecoverTransactionServiceImpl;
import com.happylifeplat.transaction.admin.service.recover.MongoRecoverTransactionServiceImpl;
import com.happylifeplat.transaction.admin.service.recover.RedisRecoverTransactionServiceImpl;
import com.happylifeplat.transaction.admin.service.recover.ZookeeperRecoverTransactionServiceImpl;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiaoyu
 */
@Configuration
public class RecoverConfiguration {

    /**
     * spring.profiles.active = {}
     */
    @Configuration
    @Profile("jdbc")
    static class JdbcRecoverConfiguration {

        private final Environment env;

        @Autowired
        public JdbcRecoverConfiguration(Environment env) {
            this.env = env;
        }

        @Bean
        public DataSource dataSource() {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(env.getProperty("recover.db.driver"));
            dataSource.setUrl(env.getProperty("recover.db.url"));
            //用户名
            dataSource.setUsername(env.getProperty("recover.db.username"));
            //密码
            dataSource.setPassword(env.getProperty("recover.db.password"));
            dataSource.setInitialSize(2);
            dataSource.setMaxActive(20);
            dataSource.setMinIdle(0);
            dataSource.setMaxWait(60000);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTestOnBorrow(false);
            dataSource.setTestWhileIdle(true);
            dataSource.setPoolPreparedStatements(false);
            return dataSource;
        }

        @Bean
        @Qualifier("jdbcTransactionRecoverService")
        public RecoverTransactionService jdbcTransactionRecoverService() {
            JdbcRecoverTransactionServiceImpl jdbcTransactionRecoverService = new JdbcRecoverTransactionServiceImpl();
            jdbcTransactionRecoverService.setDbType(env.getProperty("recover.db.driver"));
            return jdbcTransactionRecoverService;
        }


    }


    @Configuration
    @Profile("redis")
    static class RedisRecoverConfiguration {

        private final Environment env;

        @Autowired
        public RedisRecoverConfiguration(Environment env) {
            this.env = env;
        }


        private JedisConnectionFactory getConnectionFactory() {
            JedisConnectionFactory factory = new JedisConnectionFactory();
            factory.setHostName(env.getProperty("recover.redis.hostName"));
            factory.setPassword(env.getProperty("recover.redis.password"));
            factory.setPort(Integer.valueOf(env.getProperty("recover.redis.port")));
            factory.setPoolConfig(new JedisPoolConfig());
            return factory;
        }

        @Bean
        @SuppressWarnings("unchecked")
        public RedisTemplate redisTemplate() {
            RedisTemplate redisTemplate = new StringRedisTemplate();
            redisTemplate.setConnectionFactory(getConnectionFactory());
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                    new Jackson2JsonRedisSerializer(Object.class);
            redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }

        @Bean
        @Qualifier("redisTransactionRecoverService")
        public RecoverTransactionService redisTransactionRecoverService() {
            return new RedisRecoverTransactionServiceImpl(redisTemplate());
        }


    }

    @Configuration
    @Profile("file")
    static class FileRecoverConfiguration {

        @Bean
        @Qualifier("fileTransactionRecoverService")
        public RecoverTransactionService fileTransactionRecoverService() {
            return new FileRecoverTransactionServiceImpl();
        }

    }

    @Configuration
    @Profile("zookeeper")
    static class ZookeeperRecoverConfiguration {

        private final Environment env;

        @Autowired
        public ZookeeperRecoverConfiguration(Environment env) {
            this.env = env;
        }

        private static final  Lock LOCK = new ReentrantLock();


        @Bean
        @Qualifier("zookeeperTransactionRecoverService")
        public RecoverTransactionService zookeeperTransactionRecoverService() {
            ZooKeeper zooKeeper = null;
            try {
                final String host = env.getProperty("recover.zookeeper.host");
                final String sessionTimeOut = env.getProperty("recover.zookeeper.sessionTimeOut");
                zooKeeper = new ZooKeeper(host, Integer.parseInt(sessionTimeOut), watchedEvent -> {
                    if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                        // 放开闸门, wait在connect方法上的线程将被唤醒
                        LOCK.unlock();
                    }
                });
                LOCK.lock();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return new ZookeeperRecoverTransactionServiceImpl(zooKeeper);
        }

    }

    @Configuration
    @Profile("mongo")
    static class MongoRecoverConfiguration {

        private final Environment env;

        @Autowired
        public MongoRecoverConfiguration(Environment env) {
            this.env = env;
        }

        @Bean
        @Qualifier("mongoTransactionRecoverService")
        public RecoverTransactionService mongoTransactionRecoverService() {

            MongoClientFactoryBean clientFactoryBean = new MongoClientFactoryBean();
            MongoCredential credential = MongoCredential.createScramSha1Credential(
                    env.getProperty("recover.mongo.userName"),
                    env.getProperty("recover.mongo.dbName"),
                    env.getProperty("recover.mongo.password").toCharArray());
            clientFactoryBean.setCredentials(new MongoCredential[]{
                    credential
            });
            List<String> urls = Splitter.on(",").trimResults().splitToList(env.getProperty("recover.mongo.url"));
            ServerAddress[] sds = new ServerAddress[urls.size()];
            for (int i = 0; i < sds.length; i++) {
                List<String> adds = Splitter.on(":").trimResults().splitToList(urls.get(i));
                InetSocketAddress address = new InetSocketAddress(adds.get(0), Integer.parseInt(adds.get(1)));
                sds[i] = new ServerAddress(address);
            }
            clientFactoryBean.setReplicaSetSeeds(sds);
            MongoTemplate mongoTemplate = null;
            try {
                mongoTemplate = new MongoTemplate(clientFactoryBean.getObject(), env.getProperty("recover.mongo.dbName"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new MongoRecoverTransactionServiceImpl(mongoTemplate);
        }

    }

}
