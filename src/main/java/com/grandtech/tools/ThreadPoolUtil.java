package com.grandtech.tools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zy on 2016/10/21.
 * 线程池帮助类
 */

public class ThreadPoolUtil {
    //最大可用的CPU核数
    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    //线程最大的空闲存活时间，单位为秒
    public static final int KEEPALIVETIME = 1;
    //任务缓存队列长度
    public static final int BLOCKINGQUEUE_LENGTH = 6000;

    //线程池
    private static ThreadPoolExecutor threadPool;
    // 创建一个线程池，使用LinkedBlockingQueue作为任务队列
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

    private ThreadPoolUtil() {
    }

    //线程工厂
    private static ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
        }
    };

    static {
        threadPool = new ThreadPoolExecutor(PROCESSORS * 2, PROCESSORS * 4, KEEPALIVETIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(BLOCKINGQUEUE_LENGTH));
    }

    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static void execute(FutureTask futureTask) {
         threadPool.execute(futureTask);

    }

    public static void cancel(FutureTask futureTask) {
        futureTask.cancel(true);
    }
}
