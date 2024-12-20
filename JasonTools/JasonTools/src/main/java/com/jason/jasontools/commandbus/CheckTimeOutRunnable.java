package com.jason.jasontools.commandbus;

/**
 * <p>
 * 描述: 超时检查类
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月17日
 */
public class CheckTimeOutRunnable implements Runnable {

    private long startTime = 0;
    private AbsCommand command = null;
    // 是否检查超时
    private boolean isCheck = true;

    public CheckTimeOutRunnable(AbsCommand command) {
        this.command = command;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        //检查此命令发送是否超时
        while (isCheck) {
            //未超时
            if (System.currentTimeMillis() - startTime < command.getMaxTimeout()) {
                try {
                    Thread.sleep(1000);
                    continue;
                } catch (Exception ignore) {
                    isCheck = false;
                    break;
                }
            }
            command.timeoutOccurs();
            break;
        }
    }

    /**
     * 停止超时监听
     */
    public void stopCheckTimeOutThread() {
        isCheck = false;
    }
}
