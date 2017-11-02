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

import com.happylifeplat.transaction.admin.helper.PageHelper;
import com.happylifeplat.transaction.admin.page.CommonPager;
import com.happylifeplat.transaction.admin.page.PageParameter;
import com.happylifeplat.transaction.admin.query.RecoverTransactionQuery;
import com.happylifeplat.transaction.admin.service.RecoverTransactionService;
import com.happylifeplat.transaction.admin.vo.TransactionRecoverVO;
import com.happylifeplat.transaction.common.bean.TransactionInvocation;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.holder.DateUtils;
import com.happylifeplat.transaction.common.holder.DbTypeUtils;
import com.happylifeplat.transaction.common.holder.RepositoryPathUtils;
import com.happylifeplat.transaction.common.serializer.ObjectSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Description: .</p>
 * jdbc实现
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/19 17:08
 * @since JDK 1.8
 */
public class JdbcRecoverTransactionServiceImpl implements RecoverTransactionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String dbType;


    /**
     * 分页获取补偿事务信息
     *
     * @param query 查询条件
     * @return CommonPager<TransactionRecoverVO>
     */
    @Override
    public CommonPager<TransactionRecoverVO> listByPage(RecoverTransactionQuery query) {
        final String tableName = RepositoryPathUtils.buildDbTableName(query.getApplicationName());
        final PageParameter pageParameter = query.getPageParameter();

        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("select id,target_class,target_method," +
                " retried_count,create_time,last_time,version,group_id,task_id from ")
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

            pager.setDataList(mapList.stream().map(this::buildByMap).collect(Collectors.toList()));
        }

        final Integer totalCount =
                jdbcTemplate.queryForObject("select count(1) from " + tableName, Integer.class);


        pager.setPage(PageHelper.buildPage(pageParameter, totalCount));

        return pager;
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
        final String tableName = RepositoryPathUtils.buildDbTableName(applicationName);
        ids.stream()
                .map(id -> buildDelSql(tableName, id))
                .forEach(sql -> jdbcTemplate.execute(sql));

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
            return false;
        }
        final String tableName = RepositoryPathUtils.buildDbTableName(applicationName);

        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("update ").append(tableName)
                .append("  set retried_count = ")
                .append(retry).append(",last_time= '")
                .append(DateUtils.getCurrentDateTime()).append("'")
                .append(" where id =").append(id);

        jdbcTemplate.execute(sqlBuilder.toString());
        return Boolean.TRUE;
    }


    private TransactionRecoverVO buildByMap(Map<String, Object> map) {
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

    private String buildPageSql(String sql, PageParameter pageParameter) {
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

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = DbTypeUtils.buildByDriverClassName(dbType);
    }

    private String buildDelSql(String tableName, String id) {
        return "DELETE FROM " + tableName + " WHERE ID=" + id;
    }
}
