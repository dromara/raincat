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

package com.raincat.admin.service.recover;

import com.google.common.collect.Sets;
import com.raincat.admin.helper.ConvertHelper;
import com.raincat.admin.helper.PageHelper;
import com.raincat.admin.page.CommonPager;
import com.raincat.admin.query.RecoverTransactionQuery;
import com.raincat.admin.service.RecoverTransactionService;
import com.raincat.admin.vo.TransactionRecoverVO;
import com.raincat.common.bean.adapter.TransactionRecoverAdapter;
import com.raincat.common.exception.TransactionException;
import com.raincat.common.holder.DateUtils;
import com.raincat.common.holder.RepositoryPathUtils;
import com.raincat.common.jedis.JedisClient;
import com.raincat.common.serializer.ObjectSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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

        final int currentPage = query.getPageParameter().getCurrentPage();
        final int pageSize = query.getPageParameter().getPageSize();

        int start = (currentPage - 1) * pageSize;


        //transaction:recover:alipay-service:
        //获取所有的key
        Set<byte[]> keys;

        List<TransactionRecoverVO> voList;

        int totalCount;

        //如果只查 重试条件的
        if (StringUtils.isBlank(query.getTxGroupId()) && Objects.nonNull(query.getRetry())) {
            keys = jedisClient.keys((redisKey + "*").getBytes());
            final List<TransactionRecoverVO> all = findAll(keys);
            final List<TransactionRecoverVO> collect =
                    all.stream()
                            .filter(vo -> vo.getRetriedCount() < query.getRetry())
                            .collect(Collectors.toList());
            totalCount = collect.size();
            voList = collect.stream().skip(start).limit(pageSize).collect(Collectors.toList());
        } else if (StringUtils.isNoneBlank(query.getTxGroupId()) && Objects.isNull(query.getRetry())) {
            keys = Sets.newHashSet(String.join(":", redisKey, query.getTxGroupId()).getBytes());
            totalCount = keys.size();
            voList = findAll(keys);
        } else if (StringUtils.isNoneBlank(query.getTxGroupId()) && Objects.nonNull(query.getRetry())) {
            keys = Sets.newHashSet(String.join(":", redisKey, query.getTxGroupId()).getBytes());
            totalCount = keys.size();
            voList = findAll(keys)
                    .stream()
                    .filter(vo -> vo.getRetriedCount() < query.getRetry())
                    .collect(Collectors.toList());
        } else {
            keys = jedisClient.keys((redisKey + "*").getBytes());
            if (keys.size() <= 0 || keys.size() < start) {
                return commonPager;
            }
            totalCount = keys.size();
            voList = findByPage(keys, start, pageSize);
        }

        if (keys.size() <= 0 || keys.size() < start) {
            return commonPager;
        }
        commonPager.setPage(PageHelper.buildPage(query.getPageParameter(), totalCount));
        commonPager.setDataList(voList);
        return commonPager;
    }


    private List<TransactionRecoverVO> findAll(Set<byte[]> keys) {
        return keys.parallelStream()
                .map(this::buildVOByKey).filter(Objects::nonNull).collect(Collectors.toList());
    }


    private List<TransactionRecoverVO> findByPage(Set<byte[]> keys, int start, int pageSize) {
        return keys.parallelStream().skip(start).limit(pageSize)
                .map(this::buildVOByKey).filter(Objects::nonNull).collect(Collectors.toList());
    }


    private TransactionRecoverVO buildVOByKey(byte[] key) {
        final byte[] bytes = jedisClient.get(key);
        try {
            final TransactionRecoverAdapter adapter = objectSerializer.deSerialize(bytes, TransactionRecoverAdapter.class);
            return ConvertHelper.buildVO(adapter);
        } catch (TransactionException e) {
            e.printStackTrace();
            return null;
        }
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
            final TransactionRecoverAdapter adapter =
                    objectSerializer.deSerialize(bytes, TransactionRecoverAdapter.class);
            adapter.setRetriedCount(retry);
            adapter.setLastTime(DateUtils.getDateYYYY());
            jedisClient.set(key, objectSerializer.serialize(adapter));
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
