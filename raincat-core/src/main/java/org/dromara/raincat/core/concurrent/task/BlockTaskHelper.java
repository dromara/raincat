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

package org.dromara.raincat.core.concurrent.task;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import org.dromara.raincat.common.exception.TransactionRuntimeException;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.ExecutionException;

/**
 * BlockTaskHelper.
 * @author xiaoyu
 */
public final class BlockTaskHelper {

    private static final int MAX_COUNT = 10000;

    private static final BlockTaskHelper BLOCK_TASK_HELPER = new BlockTaskHelper();

    private static final LoadingCache<String, BlockTask> LOADING_CACHE = CacheBuilder.newBuilder()
            .maximumWeight(MAX_COUNT)
            .weigher((Weigher<String, BlockTask>) (string, blockTask) -> getSize())
            .build(new CacheLoader<String, BlockTask>() {
                @Override
                public BlockTask load(final String key) {
                    return createTask(key);
                }
            });

    private BlockTaskHelper() {

    }

    public static BlockTaskHelper getInstance() {
        return BLOCK_TASK_HELPER;
    }

    private static int getSize() {
        return (int) LOADING_CACHE.size();
    }

    private static BlockTask createTask(final String key) {
        BlockTask task = new BlockTask();
        task.setKey(key);
        return task;
    }

    public BlockTask getTask(final String key) {
        try {
            return LOADING_CACHE.get(key);
        } catch (ExecutionException e) {
            throw new TransactionRuntimeException(e.getCause());
        }
    }

    public void removeByKey(final String key) {
        if (StringUtils.isNotEmpty(key)) {
            LOADING_CACHE.invalidate(key);
        }
    }

}
