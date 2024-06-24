package com.jason.jasontools.commandbus;

/**
 * 重发器
 */

public interface RepeaterListener {

    /**
     * 重发同一个命令
     */
    void onRepeatCommand();

    void onNext();
}
