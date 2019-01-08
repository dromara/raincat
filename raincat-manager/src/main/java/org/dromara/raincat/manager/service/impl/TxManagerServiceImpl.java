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

package org.dromara.raincat.manager.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.common.enums.TransactionRoleEnum;
import org.dromara.raincat.common.enums.TransactionStatusEnum;
import org.dromara.raincat.common.holder.DateUtils;
import org.dromara.raincat.common.netty.bean.TxTransactionGroup;
import org.dromara.raincat.common.netty.bean.TxTransactionItem;
import org.dromara.raincat.manager.config.Constant;
import org.dromara.raincat.manager.service.TxManagerService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * TxManagerServiceImpl.
 * @author xiaoyu
 */
@Component
@SuppressWarnings("unchecked")
public class TxManagerServiceImpl implements TxManagerService {

    private final RedisTemplate redisTemplate;

    @Autowired
    public TxManagerServiceImpl(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Boolean saveTxTransactionGroup(final TxTransactionGroup txTransactionGroup) {
        try {
            final String groupId = txTransactionGroup.getId();
            //保存数据 到sortSet
            redisTemplate.opsForZSet().add(CommonConstant.REDIS_KEY_SET, groupId, CommonConstant.REDIS_SCOPE);
            final List<TxTransactionItem> itemList = txTransactionGroup.getItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                for (TxTransactionItem item : itemList) {
                    redisTemplate.opsForHash().put(cacheKey(groupId), item.getTaskKey(), item);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean addTxTransaction(final String txGroupId, final TxTransactionItem txTransactionItem) {
        try {
            redisTemplate.opsForHash().put(cacheKey(txGroupId), txTransactionItem.getTaskKey(), txTransactionItem);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<TxTransactionItem> listByTxGroupId(final String txGroupId) {
        final Map<Object, TxTransactionItem> entries =
                redisTemplate.opsForHash().entries(cacheKey(txGroupId));
        final Collection<TxTransactionItem> values = entries.values();
        return new ArrayList<>(values);
    }

    @Override
    public void removeRedisByTxGroupId(final String txGroupId) {
        redisTemplate.delete(cacheKey(txGroupId));
    }

    @Override
    public void updateTxTransactionItemStatus(final String key, final String hashKey,
                                              final int status, final Object message) {
        try {
            final TxTransactionItem item = (TxTransactionItem)
                    redisTemplate.opsForHash().get(cacheKey(key), hashKey);
            item.setStatus(status);
            if (Objects.nonNull(message)) {
                item.setMessage(message);
            }
            //计算耗时
            final String createDate = item.getCreateDate();
            final LocalDateTime now = LocalDateTime.now();
            try {
                final LocalDateTime createDateTime = DateUtils.parseLocalDateTime(createDate);
                final long consumeTime = DateUtils.getSecondsBetween(createDateTime, now);
                item.setConsumeTime(consumeTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            redisTemplate.opsForHash().put(cacheKey(key), item.getTaskKey(), item);
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int findTxTransactionGroupStatus(final String txGroupId) {
        try {
            final TxTransactionItem item = (TxTransactionItem)
                    redisTemplate.opsForHash().get(cacheKey(txGroupId), txGroupId);
            return item.getStatus();
        } catch (BeansException e) {
            e.printStackTrace();
            return TransactionStatusEnum.ROLLBACK.getCode();
        }
    }

    @Override
    public void removeCommitTxGroup() {
        final Set<String> keys = redisTemplate.keys(Constant.REDIS_KEYS);
        keys.parallelStream().forEach(key -> {
            final Map<Object, TxTransactionItem> entries = redisTemplate.opsForHash().entries(key);
            final Collection<TxTransactionItem> values = entries.values();
            final boolean present = values.stream()
                    .anyMatch(item -> item.getStatus() != TransactionStatusEnum.COMMIT.getCode());
            if (!present) {
                redisTemplate.delete(key);
            }
        });

    }

    @Override
    public void removeRollBackTxGroup() {
        final Set<String> keys = redisTemplate.keys(Constant.REDIS_KEYS);
        keys.parallelStream().forEach(key -> {
            final Map<Object, TxTransactionItem> entries = redisTemplate.opsForHash().entries(key);
            final Collection<TxTransactionItem> values = entries.values();
            final Optional<TxTransactionItem> any =
                    values.stream().filter(item -> item.getRole() == TransactionRoleEnum.START.getCode()
                            && item.getStatus() == TransactionStatusEnum.ROLLBACK.getCode())
                            .findAny();
            if (any.isPresent()) {
                redisTemplate.delete(key);
            }
        });

    }

    private String cacheKey(final String key) {
        return String.format(CommonConstant.REDIS_PRE_FIX, key);
    }
}
