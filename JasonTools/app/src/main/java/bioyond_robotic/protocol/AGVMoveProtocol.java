package bioyond_robotic.protocol;

import com.jason.jasontools.commandbus.IProtocol;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 描述: 控制AGV小车运动，并控制是否需要计算机械臂的偏移量（机械臂上的相机是否拍照）
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月11日
 */
public class AGVMoveProtocol extends IProtocol {

    /**
     * AGV运动目标位置
     */
    private String agvPointLocation;
    /**
     * Agv运动到目的地，是否需要开启拍照计算偏移量
     */
    private boolean isOpenCamera;
    /**
     * 相机拍照定位点的Id
     */
    private int visionLocationPoint;

    /**
     * @param agvPointLocation    AGV点位
     * @param isOpenCamera        Agv运动到目的地，是否需要开启拍照计算偏移量
     * @param visionLocationPoint 相机拍照定位点的Id
     */
    public AGVMoveProtocol(String agvPointLocation, boolean isOpenCamera, int visionLocationPoint) {
        this.agvPointLocation = agvPointLocation;
        this.isOpenCamera = isOpenCamera;
        this.visionLocationPoint = visionLocationPoint;
        StringBuilder builder = new StringBuilder();
        // 指令序号，可以自增也可以固定，这里暂时选择后者
        builder.append(1);
        // 固定部分
        builder.append("|/api/device/ExecuteAsync {\"CmdExecuteStatus\":1,\"Command\":\"{\\\"MethodName\\\":\\\"BY_AGVMotion\\\"");
        builder.append(",");
        // agv运动的目的地
        builder.append("\\\"agvDeviceId\\\":").append("\\\"").append(agvPointLocation).append("\\\"");
        builder.append(",");
        // Agv运动到目的地，是否需要开启拍照计算偏移量
        builder.append("\\\"isVisionCheckActivated\\\":").append(isOpenCamera);
        builder.append(",");
        // 相机拍照定位点的Id
        builder.append("\\\"visionDeviceId\\\":").append(visionLocationPoint);
        builder.append("}\",");
        builder.append("\"CommandId\":1,\"DeviceId\":\"1\",\"DeviceName\":\"YNTAGVRobot\",\"Timeout\":2000}\r");

        setProtocol(builder.toString().getBytes(StandardCharsets.UTF_8));
    }
}
