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

package com.happylifeplat.transaction.admin.service.tx;

import com.happylifeplat.transaction.admin.helper.PageHelper;
import com.happylifeplat.transaction.admin.page.CommonPager;
import com.happylifeplat.transaction.admin.page.PageParameter;
import com.happylifeplat.transaction.admin.query.TxTransactionQuery;
import com.happylifeplat.transaction.admin.service.TxTransactionItemService;
import com.happylifeplat.transaction.admin.vo.TxTransactionItemVO;
import com.happylifeplat.transaction.common.constant.CommonConstant;
import com.happylifeplat.transaction.common.enums.TransactionRoleEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/18 15:58
 * @since JDK 1.8
 */
@Service("txTransactionItemService")
@SuppressWarnings("unchecked")
public class RedisTxTransactionItemServiceImpl implements TxTransactionItemService {

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisTxTransactionItemServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CommonPager<TxTransactionItemVO> listByPage(TxTransactionQuery txTransactionQuery) {
        CommonPager<TxTransactionItemVO> commonPager = new CommonPager<>();
        //获取所有的key
        final Set<String> keys = redisTemplate.keys(CommonConstant.REDIS_KEYS);

        final int currentPage = txTransactionQuery.getPageParameter().getCurrentPage();
        final int pageSize = txTransactionQuery.getPageParameter().getPageSize();

        int start = (currentPage - 1) * pageSize;
        //int end = currentPage * pageSize;

        if (keys.size() <= 0 || keys.size() < start) {
            return commonPager;
        }
        final int totalCount = keys.size();

        commonPager.setPage(PageHelper.buildPage(txTransactionQuery.getPageParameter(),totalCount));

        keys.parallelStream().skip(start).limit(pageSize).forEach(key -> {
            final Map<Object, TxTransactionItem> entries = redisTemplate.opsForHash().entries(key);
            final Collection<TxTransactionItem> values = entries.values();
            if (CollectionUtils.isNotEmpty(values)) {
                final List<TxTransactionItemVO> itemVOS =
                        values.stream().map(this::buildVO).collect(Collectors.toList());
                commonPager.setDataList(itemVOS);
            }

        });


        return commonPager;
    }


    private TxTransactionItemVO buildVO(TxTransactionItem item) {
        TxTransactionItemVO vo = new TxTransactionItemVO();
        vo.setCreateDate(item.getCreateDate());
        vo.setModelName(item.getModelName());
        vo.setRole(TransactionRoleEnum.acquireDescByCode(item.getRole()));
        vo.setStatus(TransactionStatusEnum.acquireDescByCode(item.getStatus()));
        vo.setTargetClazzName(item.getTargetClazzName());
        vo.setTransId(item.getTransId());
        vo.setTargetMethodName(item.getTargetMethodName());
        vo.setModelName(item.getModelName());
        vo.setTmDomain(item.getTmDomain());
        vo.setTaskKey(item.getTaskKey());
        vo.setTxGroupId(item.getTxGroupId());
        vo.setWaitMaxTime(item.getWaitMaxTime());
        vo.setConsumeTime(item.getConsumeTime());
        return vo;
    }
}
