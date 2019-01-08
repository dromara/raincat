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

package org.dromara.raincat.admin.service.tx;

import com.google.common.collect.Sets;
import org.dromara.raincat.admin.helper.ConvertHelper;
import org.dromara.raincat.admin.helper.PageHelper;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.query.TxTransactionQuery;
import org.dromara.raincat.admin.service.TxTransactionGroupService;
import org.dromara.raincat.admin.vo.TxTransactionGroupVO;
import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.common.enums.TransactionRoleEnum;
import org.dromara.raincat.common.enums.TransactionStatusEnum;
import org.dromara.raincat.common.netty.bean.TxTransactionItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.raincat.admin.helper.ConvertHelper;
import org.dromara.raincat.admin.helper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis实现，用了redis sortSet来进行分页.
 * @author xiaoyu(Myth)
 */
@Service("txTransactionItemService")
@SuppressWarnings("unchecked")
public class RedisTxTransactionGroupServiceImpl implements TxTransactionGroupService {

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisTxTransactionGroupServiceImpl(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CommonPager<TxTransactionGroupVO> listByPage(final TxTransactionQuery txTransactionQuery) {
        CommonPager<TxTransactionGroupVO> commonPager = new CommonPager<>();
        final int currentPage = txTransactionQuery.getPageParameter().getCurrentPage();
        final int pageSize = txTransactionQuery.getPageParameter().getPageSize();
        int start = (currentPage - 1) * pageSize;
        int end = currentPage * pageSize;
        Set<String> keys;
        Set<String> rangeKeys;
        if (StringUtils.isNoneBlank(txTransactionQuery.getTxGroupId())) {
            keys = Sets.newHashSet(String.format(CommonConstant.REDIS_PRE_FIX, txTransactionQuery.getTxGroupId()));
            rangeKeys = Sets.newHashSet(txTransactionQuery.getTxGroupId());
        } else {
            keys = redisTemplate.keys(CommonConstant.REDIS_KEYS);
            rangeKeys = redisTemplate.opsForZSet()
                    .range(CommonConstant.REDIS_KEY_SET, start, end - 1);
        }

        if (keys.size() <= 0) {
            return commonPager;
        }
        final int totalCount = keys.size();
        commonPager.setPage(PageHelper.buildPage(txTransactionQuery.getPageParameter(), totalCount));
        final List<TxTransactionGroupVO> groupVOS = rangeKeys.stream().map((String key) -> {
            final Map<Object, TxTransactionItem> entries = redisTemplate.opsForHash()
                    .entries(String.format(CommonConstant.REDIS_PRE_FIX, key));
            final Collection<TxTransactionItem> values = entries.values();
            TxTransactionGroupVO groupVO = new TxTransactionGroupVO();
            if (CollectionUtils.isNotEmpty(values)) {
                final Optional<TxTransactionItem> first =
                        values.stream()
                                .filter(item -> TransactionRoleEnum.GROUP.getCode() == item.getRole())
                                .findFirst();
                if (first.isPresent()) {
                    final TxTransactionItem groupItem = first.get();
                    groupVO.setId(groupItem.getTaskKey());
                    groupVO.setCreateDate(groupItem.getCreateDate());
                    groupVO.setConsumeTime(groupItem.getConsumeTime());
                    groupVO.setRole(TransactionRoleEnum.acquireDescByCode(groupItem.getRole()));
                    groupVO.setStatus(TransactionStatusEnum.acquireDescByCode(groupItem.getStatus()));
                    groupVO.setTargetClass(groupItem.getTargetClass());
                    groupVO.setTargetMethod(groupItem.getTargetMethod());
                }
                groupVO.setItemVOList(values.stream()
                        .filter(item -> TransactionRoleEnum.GROUP.getCode() != item.getRole())
                        .map(ConvertHelper::buildTxItemVO).collect(Collectors.toList()));

            }
            return groupVO;

        }).collect(Collectors.toList());
        commonPager.setDataList(groupVOS);
        return commonPager;
    }

    @Override
    public Boolean batchRemove(final List<String> txGroupIdList) {
        if (CollectionUtils.isEmpty(txGroupIdList)) {
            return false;
        }
        try {
            for (String key : txGroupIdList) {
                redisTemplate.opsForZSet().remove(CommonConstant.REDIS_KEY_SET, key);
            }
            final List<String> keys = txGroupIdList.stream().map(txGroupId ->
                    String.format(CommonConstant.REDIS_PRE_FIX, txGroupId)).collect(Collectors.toList());
            redisTemplate.delete(keys);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
