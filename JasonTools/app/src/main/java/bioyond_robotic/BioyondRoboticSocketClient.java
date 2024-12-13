package bioyond_robotic;

import com.jason.jasontools.JasonSocket.JasonSocketClient;
import com.jason.jasontools.JasonSocket.JasonSocketReceiver;
import com.jason.jasontools.JasonSocket.JasonSocketSender;
import com.jason.jasontools.serialport.AbsVerifySerialProtocolData;
import com.jason.jasontools.util.LogUtil;

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
public class BioyondRoboticSocketClient {
    private String ip;
    private int protocol;
    private int connectTimeout;
    private JasonSocketClient socketClient;
    private JasonSocketSender sender;
    private JasonSocketReceiver receiver;


    private static class Single {
        private static BioyondRoboticSocketClient client = new BioyondRoboticSocketClient();
    }

    public static BioyondRoboticSocketClient getInstance() {
        return Single.client;
    }

    public void connect(String ip, int protocol, int connectTimeout) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BioyondRoboticSocketClient.this.ip = ip;
                BioyondRoboticSocketClient.this.protocol = protocol;
                BioyondRoboticSocketClient.this.connectTimeout = connectTimeout;
                BioyondRoboticSocketClient.this.socketClient = new JasonSocketClient(ip, protocol);
                try {
                    socketClient.connect(connectTimeout);
                } catch (Exception e) {
                    LogUtil.i("远程服务 " + ip + ":" + protocol + "连接失败");
                    return;
                }
                AbsVerifySerialProtocolData verifySerialProtocolData = new BioyondVerifyProtocol();

                // 对发送者进行资源设置
                BioyondRoboticSocketClient.this.sender = new JasonSocketSender()
                        .setSocketClient(BioyondRoboticSocketClient.this.socketClient)
                        .setVerifySerialProtocolData(verifySerialProtocolData);

                // 对接收者进行资源设置
                BioyondRoboticSocketClient.this.receiver = new JasonSocketReceiver()
                        .setSocketClient(socketClient)
                        .setVerifySerialProtocolData(verifySerialProtocolData)
                        .setParseSerialProtocolData(new ParseRoboticProtocol())
                        .starReceiverData();
            }
        }).start();
    }

    public void connect() {
        connect(ip, protocol, connectTimeout);
    }

    public JasonSocketSender getSender() {
        return sender;
    }

    public JasonSocketReceiver getReceiver() {
        return receiver;
    }

    public void onDestroy() {
        // todo 未实现
        sender.onDestroy();
        receiver.onDestroy();
        socketClient.close();
    }
}
