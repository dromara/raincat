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

package org.dromara.raincat.common.netty.bean;

import org.dromara.raincat.common.enums.NettyMessageActionEnum;
import org.dromara.raincat.common.enums.NettyResultEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * HeartBeat.
 * @author xiaoyu
 */
@Data
public class HeartBeat implements Serializable {

    private static final long serialVersionUID = 4183978848464761529L;

    /**
     * 执行动作. {@linkplain NettyMessageActionEnum}
     */
    private int action;

    /**
     * 执行发送数据任务task key.
     */
    private String key;

    /**
     * result. {@linkplain NettyResultEnum}
     */
    private int result;

    /**
     * 事务组信息.
     */
    private TxTransactionGroup txTransactionGroup;

}
