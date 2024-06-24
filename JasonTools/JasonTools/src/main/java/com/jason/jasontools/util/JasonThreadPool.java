package com.jason.jasontools.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: 线程池，线程池大小为2*CPU核数，最大线程数为4*CPU核数
 * 任务队列为LinkedBlockingQueue，队列大小为1024
 * 线程池拒绝策略为DiscardPolicy
 * 线程池预热启动
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月24日
 */
public class JasonThreadPool extends ThreadPoolExecutor {

    public static class Single{
        private static JasonThreadPool instance = new JasonThreadPool(
                2*Runtime.getRuntime().availableProcessors(),
                4*Runtime.getRuntime().availableProcessors(),
                5000,
                TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(1024),
                getMyThreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());
        static {
            instance.prestartCoreThread();
        }
    }
    public static JasonThreadPool getInstance(){
        return Single.instance;
    }
    public JasonThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public JasonThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public JasonThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public JasonThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }


    public static ThreadFactory getMyThreadFactory(){
        return  new ThreadFactory() {
            private long count=0;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread=new Thread(r);
                thread.setName("JasonThreadPool" + count++);
                return thread;
            }
        };
    }
}
