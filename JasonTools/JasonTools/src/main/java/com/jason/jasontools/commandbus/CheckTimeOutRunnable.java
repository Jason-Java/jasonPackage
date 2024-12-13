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
            //超时
            //判断此命令是否需要重发
            //重命令最多重发三次,在重发命令期间不需要通知用户超时
            if (command.getRepeater() && command.getRepeatCount() < 2) {
                command.setRepeatCount(command.getRepeatCount() + 1);
                command.getRepeaterListener().onRepeatCommand();
            } else {
                if (command.getMessageListener() != null) {
                    command.getMessageListener().error("超时", -1);
                    command.setMessageListener(null);
                }
                // todo 去掉超时自动调度下一个命令
                command.getRepeaterListener().onNext();
                command.setRepeaterListener(null);
            }
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
