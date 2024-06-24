package com.jason.jasontools.commandbus;

import com.jason.jasontools.util.LogUtil;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月28日
 */
public class CommandExecute implements Runnable {
    private String TAG = "JasonBaseCommandExecuteTag";
    private final int QueueCapacity = 100;
    private LinkedList<AbsCommand> blockingQueue;
    //队列锁
    private final Lock lockAddQueue = new ReentrantLock();
    private final Condition conditionAddQueue = lockAddQueue.newCondition();
    //执行锁
    private final Lock lockExecute = new ReentrantLock();
    private final Condition conditionExecute = lockExecute.newCondition();
    private volatile boolean isRun = true;

    public CommandExecute() {
        blockingQueue = new LinkedList<AbsCommand>();
    }

    public CommandExecute(String TAG) {
        blockingQueue = new LinkedList<AbsCommand>();
        this.TAG = TAG;
    }


    public void addQueue(AbsCommand command) {
        lockAddQueue.lock();
        try {
            if (blockingQueue.size() >= QueueCapacity) {
                blockingQueue.remove(0);
            }
            int i = 0;
            for (; i < blockingQueue.size(); i++) {
                AbsCommand first = blockingQueue.peek();
                if (first == null) {
                    blockingQueue.add(command);
                    break;
                } else if (command.getPriority() > blockingQueue.peek().getPriority()) {
                    blockingQueue.add(i + 1, command);
                    break;
                }
            }
            if (i == blockingQueue.size()) {
                blockingQueue.add(command);
                conditionAddQueue.signalAll();
            }
        } catch (Exception ignore) {
            LogUtil.e(TAG, "队列已满,添加线程已阻塞");
        } finally {
            lockAddQueue.unlock();
        }
    }

    @Override
    public void run() {
        LogUtil.i(TAG, "线程启动");
        while (!Thread.currentThread().isInterrupted() && isRun) {
            AbsCommand command = getCommand();
            if (command == null) {
                return;
            }
            sendCommand(command);
        }
    }

    /**
     * 获取命令
     *
     * @return
     */
    private AbsCommand getCommand() {
        lockAddQueue.lock();
        try {
            while (blockingQueue.isEmpty()) {
                conditionAddQueue.await(5, TimeUnit.SECONDS);
            }
            return blockingQueue.pollFirst();
        } catch (InterruptedException e) {
        } finally {
            lockAddQueue.unlock();
        }
        return null;
    }

    private void sendCommand(final AbsCommand command) {
        lockExecute.lock();
        try {
            command.execute(new RepeaterListener() {
                @Override
                public void onRepeatCommand() {
                    command.execute(this);
                }

                @Override
                public void onNext() {
                    lockExecute.lock();
                    // 唤醒线程
                    try {
                        conditionExecute.signalAll();
                    } finally {
                        lockExecute.unlock();
                    }
                }
            });
            conditionExecute.await();
        } catch (InterruptedException e) {

        } finally {
            lockExecute.unlock();
        }
    }

    // 停止线程
    public void stop() {
        isRun = false;
    }
}
