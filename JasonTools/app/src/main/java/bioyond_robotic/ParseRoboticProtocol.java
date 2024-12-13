package bioyond_robotic;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.IParseSerialProtocol;
import com.jason.jasontools.serialport.ResultData;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月12日
 */
public class ParseRoboticProtocol extends IParseSerialProtocol {

    @Override
    protected ResultData<String> parseData(IProtocol protocol, int len) {
        byte[] protocol1 = protocol.getProtocol();
        String s = new String(protocol1, StandardCharsets.UTF_8);
        ResultData<String> resultData = new ResultData<>();
        resultData.setData(s);
        resultData.setSuccess(true);
        resultData.setProtocol(protocol);
        return resultData;
    }

    @Override
    protected boolean isParseData(IProtocol protocol, int len) {
        return true;
    }
}
