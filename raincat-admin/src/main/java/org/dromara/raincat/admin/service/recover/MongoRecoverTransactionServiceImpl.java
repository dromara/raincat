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

import com.mongodb.WriteResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.raincat.admin.helper.ConvertHelper;
import org.dromara.raincat.admin.helper.PageHelper;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.page.PageParameter;
import org.dromara.raincat.admin.query.RecoverTransactionQuery;
import org.dromara.raincat.admin.service.RecoverTransactionService;
import org.dromara.raincat.admin.vo.TransactionRecoverVO;
import org.dromara.raincat.common.bean.adapter.MongoAdapter;
import org.dromara.raincat.common.exception.TransactionRuntimeException;
import org.dromara.raincat.common.holder.DateUtils;
import org.dromara.raincat.common.holder.RepositoryPathUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mongodb impl.
 * @author xiaoyu(Myth)
 */
public class MongoRecoverTransactionServiceImpl implements RecoverTransactionService {

    private MongoTemplate mongoTemplate;

    public MongoRecoverTransactionServiceImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CommonPager<TransactionRecoverVO> listByPage(final RecoverTransactionQuery query) {
        CommonPager<TransactionRecoverVO> voCommonPager = new CommonPager<>();
        final String mongoTableName = RepositoryPathUtils.buildMongoTableName(query.getApplicationName());
        final PageParameter pageParameter = query.getPageParameter();
        final int currentPage = pageParameter.getCurrentPage();
        final int pageSize = pageParameter.getPageSize();
        Query baseQuery = new Query();
        if (StringUtils.isNoneBlank(query.getTxGroupId())) {
            baseQuery.addCriteria(new Criteria("groupId").is(query.getTxGroupId()));
        }
        if (Objects.nonNull(query.getRetry())) {
            baseQuery.addCriteria(new Criteria("retriedCount").lt(query.getRetry()));
        }
        final long totalCount = mongoTemplate.count(baseQuery, mongoTableName);
        if (totalCount <= 0) {
            return voCommonPager;
        }
        int start = (currentPage - 1) * pageSize;
        voCommonPager.setPage(PageHelper.buildPage(query.getPageParameter(), (int) totalCount));
        baseQuery.skip(start).limit(pageSize);
        final List<MongoAdapter> mongoAdapters =
                mongoTemplate.find(baseQuery, MongoAdapter.class, mongoTableName);
        if (CollectionUtils.isNotEmpty(mongoAdapters)) {
            final List<TransactionRecoverVO> recoverVOS =
                    mongoAdapters
                            .stream()
                            .map(ConvertHelper::buildVO)
                            .collect(Collectors.toList());
            voCommonPager.setDataList(recoverVOS);
        }
        return voCommonPager;
    }

    @Override
    public Boolean batchRemove(final List<String> ids, final String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }
        final String mongoTableName = RepositoryPathUtils.buildMongoTableName(applicationName);
        ids.forEach(id -> {
            Query query = new Query();
            query.addCriteria(new Criteria("transId").is(id));
            mongoTemplate.remove(query, mongoTableName);
        });

        return Boolean.TRUE;
    }

    @Override
    public Boolean updateRetry(final String id, final Integer retry, final String applicationName) {
        if (StringUtils.isBlank(id)
                || StringUtils.isBlank(applicationName)
                || Objects.isNull(retry)) {
            return Boolean.FALSE;
        }
        final String mongoTableName = RepositoryPathUtils.buildMongoTableName(applicationName);

        Query query = new Query();
        query.addCriteria(new Criteria("transId").is(id));
        Update update = new Update();
        update.set("lastTime", DateUtils.getCurrentDateTime());
        update.set("retriedCount", retry);
        final WriteResult writeResult = mongoTemplate.updateFirst(query, update,
                MongoAdapter.class, mongoTableName);
        if (writeResult.getN() <= 0) {
            throw new TransactionRuntimeException("更新数据异常!");
        }
        return Boolean.TRUE;
    }

}
