package bioyond_robotic.protocol;

import com.jason.jasontools.commandbus.IProtocol;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 描述:控制机械臂抓取或放置东西
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月11日
 */
public class RoboticArmPickOrPlace extends IProtocol {
    //堆栈Id
    private int deviceId;
    // 堆栈 x位置
    private int posX;
    // 堆栈 y位置
    private int posY;
    // 堆栈 z位置
    private int posZ;
    // 抓取（1）或者放置（2）
    private int pickOrPlace;
    // 物料类型
    private int robotObjType;

    public RoboticArmPickOrPlace(int deviceId, int posX, int posY, int posZ, int pickOrPlace, int robotObjType) {
        this.deviceId = deviceId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.pickOrPlace = pickOrPlace;
        this.robotObjType = robotObjType;
        StringBuilder builder = new StringBuilder();
        // 指令序号，可以自增也可以固定，这里暂时选择后者
        builder.append(2);
        // 固定部分
        builder.append("|/api/device/ExecuteAsync {\"CmdExecuteStatus\":1,\"Command\":\"{\\\"MethodName\\\":\\\"BY_RobotPickOrPlace\\\"");
        builder.append(",");
        builder.append("\\\"deviceId\\\":").append(deviceId);
        builder.append(",");
        builder.append("\\\"posX\\\":").append(posX);
        builder.append(",");
        builder.append("\\\"posY\\\":").append(posY);
        builder.append(",");
        builder.append("\\\"posZ\\\":").append(posZ);
        builder.append(",");
        builder.append("\\\"robotMoveType\\\":").append(pickOrPlace);
        builder.append(",");
        builder.append("\\\"robotObjType\\\":").append(robotObjType);
        builder.append("}\",");
        builder.append("\"CommandId\":1,\"DeviceId\":\"1\",\"DeviceName\":\"YNTAGVRobot\",\"Timeout\":2000}\r");
        setProtocol(builder.toString().getBytes(StandardCharsets.UTF_8));
    }
}
