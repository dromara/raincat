/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dromara.raincat.core.disruptor.handler;

import com.lmax.disruptor.WorkHandler;
import org.dromara.raincat.common.enums.CompensationActionEnum;
import org.dromara.raincat.core.compensation.TxCompensationService;
import org.dromara.raincat.core.disruptor.event.TxTransactionEvent;
import org.dromara.raincat.core.disruptor.event.TxTransactionEvent;

import java.util.concurrent.Executor;

/**
 * disruptor handler.
 *
 * @author xiaoyu(Myth)
 */
public class TxTransactionEventHandler implements WorkHandler<TxTransactionEvent> {

    private final TxCompensationService txCompensationService;

    private final Executor executor;

    public TxTransactionEventHandler(final Executor executor, final TxCompensationService txCompensationService) {
        this.executor = executor;
        this.txCompensationService = txCompensationService;
    }

    @Override
    public void onEvent(final TxTransactionEvent txTransactionEvent) {
        executor.execute(() -> {
            if (txTransactionEvent.getType() == CompensationActionEnum.SAVE.getCode()) {
                txCompensationService.save(txTransactionEvent.getTransactionRecover());
            } else if (txTransactionEvent.getType() == CompensationActionEnum.DELETE.getCode()) {
                txCompensationService.remove(txTransactionEvent.getTransactionRecover().getId());
            } else if (txTransactionEvent.getType() == CompensationActionEnum.UPDATE.getCode()) {
                txCompensationService.update(txTransactionEvent.getTransactionRecover());
            } else if (txTransactionEvent.getType() == CompensationActionEnum.COMPENSATE.getCode()) {
                txCompensationService.compensation(txTransactionEvent.getTransactionRecover());
            }
            txTransactionEvent.clear();
        });

    }
}
