package com.jk51.modules.integral.timingtask.util;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-06-01 14:17
 * 修改记录:
 */
public class ThreadPools {

    private static Map<Class<?>, ExecutorService> MAP = new Hashtable();

    public ThreadPools() {
    }

    public static ExecutorService getThreadPool(Object key, int thread) {
        Class<?> k = key.getClass();
        ExecutorService pool = null;
        pool = (ThreadPoolExecutor) MAP.get(k);
        if (pool == null) {

            pool = Executors.newCachedThreadPool();
//            pool = new ThreadPoolExecutor(thread, thread, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(1), new ThreadPoolExecutor.DiscardPolicy());
            MAP.put(k, pool);
        }

        return pool;
    }
}
