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

package org.dromara.raincat.admin.service.recover;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.raincat.admin.helper.ConvertHelper;
import org.dromara.raincat.admin.helper.PageHelper;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.query.RecoverTransactionQuery;
import org.dromara.raincat.admin.service.RecoverTransactionService;
import org.dromara.raincat.admin.vo.TransactionRecoverVO;
import org.dromara.raincat.common.bean.adapter.TransactionRecoverAdapter;
import org.dromara.raincat.common.exception.TransactionException;
import org.dromara.raincat.common.holder.DateUtils;
import org.dromara.raincat.common.holder.RepositoryPathUtils;
import org.dromara.raincat.common.jedis.JedisClient;
import org.dromara.raincat.common.serializer.ObjectSerializer;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis impl.
 *
 * @author xiaoyu(Myth)
 */
@SuppressWarnings("unchecked")
public class RedisRecoverTransactionServiceImpl implements RecoverTransactionService {

    private ObjectSerializer objectSerializer;

    private JedisClient jedisClient;

    public RedisRecoverTransactionServiceImpl(final JedisClient jedisClient, final ObjectSerializer objectSerializer) {
        this.jedisClient = jedisClient;
        this.objectSerializer = objectSerializer;
    }

    @Override
    public CommonPager<TransactionRecoverVO> listByPage(final RecoverTransactionQuery query) {
        CommonPager<TransactionRecoverVO> commonPager = new CommonPager<>();
        final String redisKey = RepositoryPathUtils.buildRedisKey(query.getApplicationName());
        final int currentPage = query.getPageParameter().getCurrentPage();
        final int pageSize = query.getPageParameter().getPageSize();
        int start = (currentPage - 1) * pageSize;
        Set<byte[]> keys;
        List<TransactionRecoverVO> voList;
        int totalCount;
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

    private List<TransactionRecoverVO> findAll(final Set<byte[]> keys) {
        return keys.parallelStream()
                .map(this::buildVOByKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<TransactionRecoverVO> findByPage(final Set<byte[]> keys, final int start, final int pageSize) {
        return keys.parallelStream()
                .skip(start).limit(pageSize)
                .map(this::buildVOByKey)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private TransactionRecoverVO buildVOByKey(final byte[] key) {
        final byte[] bytes = jedisClient.get(key);
        final TransactionRecoverAdapter adapter;
        try {
            adapter = objectSerializer.deSerialize(bytes, TransactionRecoverAdapter.class);
            return ConvertHelper.buildVO(adapter);
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean batchRemove(final List<String> ids, final String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }
        String keyPrefix = RepositoryPathUtils.buildRedisKey(applicationName);
        final String[] keys = ids.stream()
                .map(id -> cacheKey(keyPrefix, id)).toArray(String[]::new);
        jedisClient.del(keys);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateRetry(final String id, final Integer retry, final String applicationName) {
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

    private String cacheKey(final String keyPrefix, final String id) {
        return String.join(":", keyPrefix, id);
    }

}
