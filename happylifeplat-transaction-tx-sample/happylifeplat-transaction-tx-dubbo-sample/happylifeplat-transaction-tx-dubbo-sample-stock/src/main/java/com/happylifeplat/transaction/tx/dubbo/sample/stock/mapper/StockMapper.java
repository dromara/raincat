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
package com.happylifeplat.transaction.tx.dubbo.sample.stock.mapper;

import com.happylifeplat.transaction.tx.dubbo.sample.stock.api.entity.Stock;

/**
 * @author xiaoyu
 */
public interface StockMapper {

    /**
     * 保存库存
     *
     * @param stock 库存实体
     */
    void save(Stock stock);


    /**
     * 更新
     *
     * @param stock 库存实体
     */
    void updateNumber(Stock stock);
}
