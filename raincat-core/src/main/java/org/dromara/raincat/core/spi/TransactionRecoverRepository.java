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

package org.dromara.raincat.core.spi;

import org.dromara.raincat.annotation.RaincatSPI;
import org.dromara.raincat.common.bean.TransactionRecover;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.exception.TransactionRuntimeException;
import org.dromara.raincat.common.serializer.ObjectSerializer;

import java.util.Date;
import java.util.List;

/**
 * TransactionRecoverRepository.
 * @author xiaoyu
 */
@RaincatSPI
public interface TransactionRecoverRepository {

    int ROWS = 1;

    int FAIL_ROWS = 0;

    String UPDATE_EX = "update data exception！";

    /**
     * save transactionRecover.
     *
     * @param transactionRecover {@linkplain TransactionRecover}
     * @return rows
     */
    int create(TransactionRecover transactionRecover);

    /**
     * delete transactionRecover.
     *
     * @param id pk
     * @return rows
     */
    int remove(String id);

    /**
     * update transactionRecover.
     *
     * @param transactionRecover {@linkplain TransactionRecover}
     * @return rows
     * @throws TransactionRuntimeException ex
     */
    int update(TransactionRecover transactionRecover) throws TransactionRuntimeException;

    /**
     * find TransactionRecover by id.
     *
     * @param id pk
     * @return  {@linkplain TransactionRecover}
     */
    TransactionRecover findById(String id);

    /**
     * find all.
     *
     * @return TransactionRecovers
     */
    List<TransactionRecover> listAll();


    /**
     * find by delay.
     *
     * @param date delay date.
     * @return TransactionRecovers
     */
    List<TransactionRecover> listAllByDelay(Date date);


    /**
     * init.
     *
     * @param appName rpc application name.
     * @param txConfig  {@linkplain TxConfig }
     * @throws Exception ex
     */
    void init(String appName, TxConfig txConfig) throws Exception;

    /**
     * set objectSerializer spi.
     *
     * @param objectSerializer {@linkplain ObjectSerializer}
     */
    void setSerializer(ObjectSerializer objectSerializer);
}
