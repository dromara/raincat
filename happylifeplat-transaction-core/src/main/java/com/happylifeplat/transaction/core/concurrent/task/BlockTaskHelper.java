package com.happylifeplat.transaction.core.concurrent.task;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.common.holder.IdWorkerUtils;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.ExecutionException;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * BlockTaskHelper task操作帮助类
 * 采用google cache 来缓存task类 (放弃concurrentHashMap)
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 19:14
 * @since JDK 1.8
 */
public class BlockTaskHelper {

    private static final int MAX_COUNT = 10000;

    private static final BlockTaskHelper BLOCK_TASK_HELPER = new BlockTaskHelper();

    private BlockTaskHelper() {

    }

    private static final LoadingCache<String, BlockTask> cache = CacheBuilder.newBuilder()
            .maximumWeight(MAX_COUNT)
            .weigher((Weigher<String, BlockTask>) (string, BlockTask) -> getSize())
            .build(new CacheLoader<String, BlockTask>() {
                @Override
                public BlockTask load(String key) throws Exception {
                    return createTask(key);
                }
            });


    public static BlockTaskHelper getInstance() {
        return BLOCK_TASK_HELPER;
    }

    private static int getSize() {
        if (cache == null) {
            return 0;
        }
        return (int) cache.size();
    }


    private  static BlockTask createTask(String key) {
        BlockTask task = new BlockTask();
        task.setKey(key);
        return task;
    }


    /**
     * 获取task
     *
     * @param key 需要获取的key
     */
    public BlockTask getTask(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            throw new TransactionRuntimeException(e.getCause());
        }
    }


    public void removeByKey(String key) {
        if (StringUtils.isNotEmpty(key)) {
            cache.invalidate(key);
        }
    }

    public static void main(String[] args) throws ExecutionException {
        final String taskKey = IdWorkerUtils.getInstance().createTaskKey();
        final BlockTask task = BlockTaskHelper.getInstance().getTask(taskKey);
        System.out.println(task.getKey());

        BlockTaskHelper.getInstance().removeByKey(taskKey);


        System.out.println(cache.size());

        //   final BlockTask blockTask = cache.get("1576926491");

        System.out.println(cache.size());

    }

}
