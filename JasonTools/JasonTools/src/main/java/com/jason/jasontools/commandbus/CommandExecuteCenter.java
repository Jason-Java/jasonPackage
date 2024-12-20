package com.jason.jasontools.commandbus;


import com.jason.jasontools.util.JasonThreadPool;
import com.jason.jasontools.util.LogUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 命令执行中心
 */
public class CommandExecuteCenter implements Runnable {
    private static final String TAG = "CommandExecuteCenter";
    /**
     * 队列的最大容量
     */
    private static final int QueueCapacity = 100;

    /**
     * 队列
     */
    private volatile LinkedList<AbsCommand> queue;

    /**
     * 线程运行标志
     */
    private volatile boolean isSend = false;

    /**
     * 线程锁
     */
    private final Lock lock = new ReentrantLock(true);
    private Condition condition = lock.newCondition();

    private Map<String, CommandExecute> runnableMap = new HashMap<>();


    public CommandExecuteCenter() {
        init();
    }

    /**
     * 初始化队列大小
     */
    private void init() {
        queue = new LinkedList<>();
    }

    /**
     * 向发送中心队列添加指令消息
     * 若队列已满,则移除队列第一个元素,再添加
     * 根据命令的优先级,将指令添加到队列中合适的位置{@link AbsCommand#getPriority()}
     *
     * @param command
     */
    public void addQueue(AbsCommand command) {
        lock.lock();
        try {
            if (queue.size() >= QueueCapacity) {
                queue.remove(0);
            }
            int i = 0;
            for (; i < queue.size(); i++) {
                if (command.getPriority() > queue.get(i).getPriority()) {
                    queue.add(i + 1, command);
                    break;
                }
            }
            if (i == queue.size()) {
                queue.add(command);
            }
            condition.signalAll();
        } catch (Exception ignore) {
            LogUtil.e(TAG, "通风模组队列已满,添加线程已阻塞");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        LogUtil.i(TAG, "发送命令线程启动成功-------");
        isSend = true;

        while (!Thread.currentThread().isInterrupted() && isSend) {
            try {
                getRunnable();
            } catch (Exception e) {
            }
        }
        LogUtil.i(TAG, "发送命令线程结束-------");
    }

    private void getRunnable() {
        lock.lock();
        try {
            while (queue.size() == 0) {
                try {
                    condition.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                }
            }
            AbsCommand absCommand = queue.pollFirst();
            CommandExecute runnable = runnableMap.get(absCommand.getCommandName());
            if (runnable == null) {
                runnable = new CommandExecute(absCommand.getCommandName());
                runnableMap.put(absCommand.getCommandName(), runnable);
                JasonThreadPool.getInstance().execute(runnable);
            }
            runnable.addQueue(absCommand);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前命令的实际调度者
     *
     * @param  commandName
     * @return
     */
    public CommandExecute getCommandExecute(String commandName) {
        lock.lock();
        try {
            return runnableMap.get(commandName);
        } finally {
            lock.unlock();
        }

    }

    //停止线程
    public void stop() {
        isSend = false;
        Thread.currentThread().interrupt();
    }

}
