package bioyond_robotic.protocol;

import com.jason.jasontools.commandbus.IProtocol;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 描述: 如果机器人在执行任务过程中可能会包含很多衔接的步骤，如果某个步骤发生错误，发送此命令会重试错误的步骤。
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月12日
 */
public class RoboticRetryProtocol extends IProtocol {
    public RoboticRetryProtocol() {
        String cmd = "101|/api/device/ExecuteAsync {\"CmdExecuteStatus\":2,\"CommandId\":2,\"DeviceId\":\"1\",\"DeviceName\":\"YNTAGVRobot\",\"Timeout\":1800000}";
        setProtocol(cmd.getBytes(StandardCharsets.UTF_8));
    }
}
