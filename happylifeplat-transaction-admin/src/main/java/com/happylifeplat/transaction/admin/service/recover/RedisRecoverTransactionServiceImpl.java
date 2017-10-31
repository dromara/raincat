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

package com.happylifeplat.transaction.admin.service.recover;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.happylifeplat.transaction.admin.helper.ConvertHelper;
import com.happylifeplat.transaction.admin.helper.PageHelper;
import com.happylifeplat.transaction.admin.page.CommonPager;
import com.happylifeplat.transaction.admin.query.RecoverTransactionQuery;
import com.happylifeplat.transaction.admin.service.RecoverTransactionService;
import com.happylifeplat.transaction.admin.vo.TransactionRecoverVO;
import com.happylifeplat.transaction.admin.vo.TxTransactionItemVO;
import com.happylifeplat.transaction.common.bean.TransactionRecover;
import com.happylifeplat.transaction.common.bean.adapter.TransactionRecoverAdapter;
import com.happylifeplat.transaction.common.constant.CommonConstant;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.holder.RedisKeyUtils;
import com.happylifeplat.transaction.common.holder.RepositoryPathUtils;
import com.happylifeplat.transaction.common.jedis.JedisClient;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import com.happylifeplat.transaction.common.serializer.ObjectSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Description: .</p>
 * redis实现
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/19 17:08
 * @since JDK 1.8
 */
@SuppressWarnings("unchecked")
public class RedisRecoverTransactionServiceImpl implements RecoverTransactionService {


    private JedisClient jedisClient;


    public RedisRecoverTransactionServiceImpl(JedisClient jedisClient) {
        this.jedisClient = jedisClient;
    }


    @Autowired
    private ObjectSerializer objectSerializer;

    /**
     * 分页获取补偿事务信息
     *
     * @param query 查询条件
     * @return CommonPager<TransactionRecoverVO>
     */
    @Override
    public CommonPager<TransactionRecoverVO> listByPage(RecoverTransactionQuery query) {

        CommonPager<TransactionRecoverVO> commonPager = new CommonPager<>();

        final String redisKey = RepositoryPathUtils.buildRedisKey(query.getApplicationName());

        //transaction:recover:alipay-service:
        //获取所有的key
        final Set<byte[]> keys = jedisClient.keys((redisKey + "*").getBytes());


        final int currentPage = query.getPageParameter().getCurrentPage();
        final int pageSize = query.getPageParameter().getPageSize();

        int start = (currentPage - 1) * pageSize;

        if (keys.size() <= 0 || keys.size() < start) {
            return commonPager;
        }
        final int totalCount = keys.size();

        commonPager.setPage(PageHelper.buildPage(query.getPageParameter(), totalCount));

        final List<TransactionRecoverVO> recoverVOS =
                keys.parallelStream()
                        .skip(start)
                        .limit(pageSize)
                        .map(key -> {
                            final byte[] bytes = jedisClient.get(key);
                            try {
                                final TransactionRecoverAdapter adapter = objectSerializer.deSerialize(bytes, TransactionRecoverAdapter.class);
                                return ConvertHelper.buildVO(adapter);
                            } catch (TransactionException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }).filter(Objects::nonNull).collect(Collectors.toList());
        commonPager.setDataList(recoverVOS);
        return commonPager;
    }

    /**
     * 批量删除补偿事务信息
     *
     * @param ids             ids 事务id集合
     * @param applicationName 应用名称
     * @return true 成功
     */
    @Override
    public Boolean batchRemove(List<String> ids, String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }
        String keyPrefix = RepositoryPathUtils.buildRedisKey(applicationName);
        final String[] keys = ids.stream()
                .map(id -> cacheKey(keyPrefix, id)).toArray(String[]::new);

        jedisClient.del(keys);
        return Boolean.TRUE;
    }

    /**
     * 更改恢复次数
     *
     * @param id              事务id
     * @param retry           恢复次数
     * @param applicationName 应用名称
     * @return true 成功
     */
    @Override
    public Boolean updateRetry(String id, Integer retry, String applicationName) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(applicationName) || Objects.isNull(retry)) {
            return Boolean.FALSE;
        }
        String keyPrefix = RepositoryPathUtils.buildRedisKey(applicationName);
        final String key = cacheKey(keyPrefix, id);
        final byte[] bytes = jedisClient.get(key.getBytes());
        try {
            final TransactionRecover transactionRecover =
                    objectSerializer.deSerialize(bytes, TransactionRecover.class);
            transactionRecover.setRetriedCount(retry);
            transactionRecover.setLastTime(new Date());
            jedisClient.set(key, objectSerializer.serialize(transactionRecover));
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

    }

    private String cacheKey(String keyPrefix, String id) {
        return String.join(":", keyPrefix, id);
    }

}
