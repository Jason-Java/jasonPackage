package com.jason.jasonPackage;

import com.jason.jasontools.commandbus.CommandExecuteCenter;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年11月08日
 */
public class CommendExecuteCenterSingle extends CommandExecuteCenter {
    private static CommandExecuteCenter instance = new CommendExecuteCenterSingle();

    public static CommandExecuteCenter getInstance() {
        return instance;
    }
}
