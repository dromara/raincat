/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.raincat.core.mediator;

import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.core.concurrent.threadlocal.TxTransactionLocal;

/**
 * The type RpcMediator.
 *
 * @author xiaoyu(Myth)
 */
public class RpcMediator {

    private static final RpcMediator RPC_MEDIATOR = new RpcMediator();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RpcMediator getInstance() {
        return RPC_MEDIATOR;
    }

    /**
     * Transmit.
     *
     * @param rpcTransmit the rpc mediator
     */
    public void transmit(final RpcTransmit rpcTransmit) {
        rpcTransmit.transmit(CommonConstant.TX_TRANSACTION_GROUP,
                TxTransactionLocal.getInstance().getTxGroupId());
    }

    /**
     * Acquire hmily transaction context.
     *
     * @param rpcAcquire the rpc acquire
     * @return the hmily transaction context
     */
    public String acquire(RpcAcquire rpcAcquire) {
        return rpcAcquire.acquire(CommonConstant.TX_TRANSACTION_GROUP);

    }
}
