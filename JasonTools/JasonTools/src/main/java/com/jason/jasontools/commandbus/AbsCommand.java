package com.jason.jasontools.commandbus;


import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.util.EErrorNumber;
import com.jason.jasontools.util.JasonThreadPool;
import com.jason.jasontools.util.LogUtil;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年05月23日
 */
public abstract class AbsCommand {

    /**
     * 命令执行的优先级
     * 数值越小优先级越高
     */
    private int priority = Integer.MAX_VALUE;
    /**
     * 重复次数
     */
    private int repeatCount = 0;

    /**
     * 超时时间最大值
     */
    private int maxTimeout = 2000;

    /**
     * 消息监听器
     */
    private IMessageListener messageListener;
    /**
     * 重发机制监听器
     */
    private RepeaterListener repeaterListener;

    /**
     * 检查超时线程
     */
    private CheckTimeOutRunnable checkTimeOutRunnable;
    protected IProtocol iProtocol;


    public AbsCommand(IMessageListener messageListener, IProtocol iProtocol) {
        this.messageListener = messageListener;
        this.iProtocol = iProtocol;
    }

    /**
     * 执行命令
     * 1.首先检查重发监听器是否为空,如果为空则抛出异常
     * 2.启动检查超时线程
     * 3.执行命令
     *
     * @param listener
     */
    public void execute(RepeaterListener listener) {
        this.repeaterListener = listener;
        if (listener == null) {
            throw new RuntimeException("重发机制监听器不可为空");
        }
        // 如果有重发机制，第一次发送命令的时候才调用 start()函数
        if (getRepeater() && repeatCount == 0 && messageListener != null) {
            messageListener.start();
        }
        // 如果没有重发机制，则调用start()
        else if (messageListener != null) {
            messageListener.start();
        }
        JasonThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 是否需要开启超时检查线程
                if (isStartCheckTimeOutThread()) {
                    startCheckTimeoutThread();
                }
                if (execute()) {
                    sendData();
                }
            }
        });
    }

    /**
     * 执行命令
     * 1.用户需要重写此方法<br/>
     * 2.在此方法中给命令的消费者发送命令<br/>
     * 3.如果开启了超时检查机制需要需要在消费者回调函数中调{@link #stopCheckTimeOutThread()}方法停止超时检查线程
     */
    protected abstract boolean execute();

    protected abstract void sendData();

    // 开启超时检查线程
    private void startCheckTimeoutThread() {
        JasonThreadPool.getInstance().execute(this.checkTimeOutRunnable = new CheckTimeOutRunnable(this));
    }

    /**
     * 停止超时检查线程
     *
     * @param
     */
    public void stopCheckTimeOutThread() {
        this.checkTimeOutRunnable.stopCheckTimeOutThread();
    }

    /**
     * 命令发送之后，等待接收回复数据发生超时现象
     * 判断是否需要重发此命令
     * 默认命令重发三次，在命令重发期间超时现象不需要通知用户，仅在最后一次超时情况下进行通知
     */
    protected void timeoutOccurs(String message) {
        if (this.getRepeater() && this.repeatCount < 2) {
            ++this.repeatCount;
            this.repeaterListener.onRepeatCommand();
            startCheckTimeoutThread();
        } else {
            getResultListener().error(message, EErrorNumber.READTIMEOUT.getCode());
        }
    }

    /**
     * 默认每个命令超时了都不需要重新发送
     * 如果想重发此命令可以重写此方法
     *
     * @return
     */
    public abstract boolean getRepeater();

    /**
     * 获取命令执行的优先级
     *
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 设置命令执行的优先级
     *
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }


    /**
     * 获取命令超时时间最大时间
     * 时间单位:毫秒
     * 默认为2000毫秒
     *
     * @return
     */
    public int getMaxTimeout() {
        return maxTimeout;
    }

    /**
     * 设置命令超时时间最大时间
     * 时间单位:毫秒
     *
     * @param maxTimeout
     */
    public void setMaxTimeout(int maxTimeout) {
        this.maxTimeout = maxTimeout;
    }


    /**
     * 是否开启超时检查线程
     * 默认开启
     * 如果需要关闭超时检查线程,需要重写此方法
     *
     * @return true 开启 false 不开启
     */
    protected boolean isStartCheckTimeOutThread() {
        return true;
    }

    /**
     * 获取重发监听器
     *
     * @return
     */
    public RepeaterListener getRepeaterListener() {
        return repeaterListener;
    }

    public void setRepeaterListener(RepeaterListener repeaterListener) {
        this.repeaterListener = repeaterListener;
    }

    /**
     * 获取消息监听器
     *
     * @return
     */
    public IMessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(IMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    protected abstract IResultListener getResultListener();

    // 获取命令的名称
    protected abstract String getCommandName();
}
