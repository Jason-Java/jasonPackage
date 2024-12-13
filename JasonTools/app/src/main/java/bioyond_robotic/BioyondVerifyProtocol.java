package bioyond_robotic;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.AbsVerifySerialProtocolData;
import com.jason.jasontools.serialport.VerifyFailedException;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月11日
 */
public class BioyondVerifyProtocol extends AbsVerifySerialProtocolData {
    @Override
    public IProtocol verifySendData(IProtocol protocol, int len) throws VerifyFailedException {
        byte[] data = protocol.getProtocol();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == (byte) 0x0d && i != len - 1) {
                throw new VerifyFailedException("协议中间不能有换行符");
            } else if (data[i] != (byte) 0x0d && i == len - 1) {
                throw new VerifyFailedException("协议需要以换行符为结尾");
            }
        }
        return protocol;
    }

    @Override
    public IProtocol verifyReceiveData(IProtocol protocol, int len) throws VerifyFailedException {
        return protocol;
    }
}
