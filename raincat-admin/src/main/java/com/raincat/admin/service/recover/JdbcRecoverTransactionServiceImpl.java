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

import com.raincat.admin.helper.PageHelper;
import com.raincat.admin.page.CommonPager;
import com.raincat.admin.page.PageParameter;
import com.raincat.admin.query.RecoverTransactionQuery;
import com.raincat.admin.service.RecoverTransactionService;
import com.raincat.admin.vo.TransactionRecoverVO;
import com.raincat.common.holder.DateUtils;
import com.raincat.common.holder.DbTypeUtils;
import com.raincat.common.holder.RepositoryPathUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * jdbc impl.
 * @author xiaoyu(Myth)
 */
public class JdbcRecoverTransactionServiceImpl implements RecoverTransactionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String dbType;

    @Override
    public CommonPager<TransactionRecoverVO> listByPage(final RecoverTransactionQuery query) {
        final String tableName = RepositoryPathUtils.buildDbTableName(query.getApplicationName());
        final PageParameter pageParameter = query.getPageParameter();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select id,target_class,target_method,"
                + " retried_count,create_time,last_time,version,group_id,task_id from ")
                .append(tableName).append(" where 1= 1 ");
        if (StringUtils.isNoneBlank(query.getTxGroupId())) {
            sqlBuilder.append(" and group_id = ").append(query.getTxGroupId());
        }
        if (Objects.nonNull(query.getRetry())) {
            sqlBuilder.append(" and retried_count < ").append(query.getRetry());
        }
        final String sql = buildPageSql(sqlBuilder.toString(), pageParameter);
        CommonPager<TransactionRecoverVO> pager = new CommonPager<>();
        final List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        if (CollectionUtils.isNotEmpty(mapList)) {
            pager.setDataList(mapList.stream()
                    .map(this::buildByMap).collect(Collectors.toList()));
        }
        final Integer totalCount =
                jdbcTemplate.queryForObject(String.format("select count(1) from %s", tableName), Integer.class);
        pager.setPage(PageHelper.buildPage(pageParameter, totalCount));
        return pager;
    }

    @Override
    public Boolean batchRemove(final List<String> ids, final String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }
        final String tableName = RepositoryPathUtils.buildDbTableName(applicationName);
        ids.stream()
                .map(id -> buildDelSql(tableName, id))
                .forEach(sql -> jdbcTemplate.execute(sql));
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateRetry(final String id, final Integer retry, final String applicationName) {
        if (StringUtils.isBlank(id)
                || StringUtils.isBlank(applicationName)
                || Objects.isNull(retry)) {
            return false;
        }
        final String tableName = RepositoryPathUtils.buildDbTableName(applicationName);
        String sqlBuilder =
                String.format("update %s  set retried_count = \n%d,last_time= '%s' where id =%s",
                        tableName, retry, DateUtils.getCurrentDateTime(), id);
        jdbcTemplate.execute(sqlBuilder);
        return Boolean.TRUE;
    }

    private TransactionRecoverVO buildByMap(final Map<String, Object> map) {
        TransactionRecoverVO vo = new TransactionRecoverVO();
        vo.setId((String) map.get("id"));
        vo.setRetriedCount((Integer) map.get("retried_count"));
        vo.setCreateTime(String.valueOf(map.get("create_time")));
        vo.setLastTime(String.valueOf(map.get("last_time")));
        vo.setTaskId((String) map.get("task_id"));
        vo.setGroupId((String) map.get("group_id"));
        vo.setVersion((Integer) map.get("version"));
        vo.setTargetClass((String) map.get("target_class"));
        vo.setTargetMethod((String) map.get("target_method"));
        return vo;
    }

    private String buildPageSql(final String sql, final PageParameter pageParameter) {
        switch (dbType) {
            case "mysql":
                return PageHelper.buildPageSqlForMysql(sql, pageParameter).toString();
            case "oracle":
                return PageHelper.buildPageSqlForOracle(sql, pageParameter).toString();
            case "sqlserver":
                return PageHelper.buildPageSqlForSqlserver(sql, pageParameter).toString();
            default:
                return null;
        }
    }

    public void setDbType(final String dbType) {
        this.dbType = DbTypeUtils.buildByDriverClassName(dbType);
    }

    private String buildDelSql(final String tableName, final String id) {
        return "DELETE FROM " + tableName + " WHERE ID=" + id;
    }
}
