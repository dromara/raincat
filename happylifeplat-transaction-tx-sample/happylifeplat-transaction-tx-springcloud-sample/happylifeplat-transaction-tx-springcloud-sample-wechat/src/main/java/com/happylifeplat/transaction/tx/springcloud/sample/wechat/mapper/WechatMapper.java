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
package com.happylifeplat.transaction.tx.springcloud.sample.wechat.mapper;

import com.happylifeplat.transaction.tx.springcloud.sample.wechat.entity.Wechat;
import org.apache.ibatis.annotations.Insert;

/**
 * @author xiaoyu
 */
public interface WechatMapper {


    /**
     * 保存
     *
     * @param wechat 实体对象
     * @return rows
     */
    @Insert("INSERT INTO wechat(name,amount,create_time) VALUES(#{name}, #{amount},#{createTime})")
    int save(Wechat wechat);


}
