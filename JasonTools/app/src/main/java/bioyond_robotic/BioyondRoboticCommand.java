package bioyond_robotic;

import com.jason.jasontools.commandbus.AbsCommand;
import com.jason.jasontools.commandbus.IMessageListener;
import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.serialport.ResultData;
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
public class BioyondRoboticCommand extends AbsCommand {
    private IProtocol protocol;

    public BioyondRoboticCommand(IMessageListener messageListener, IProtocol protocol) {
        super(messageListener);
        this.protocol = protocol;
    }

    @Override
    protected void execute() {
        BioyondRoboticSocketClient socketClient = BioyondRoboticSocketClient.getInstance();
        socketClient.getReceiver().setResultListener(getResultListener());
        socketClient.getSender().setResultListener(getResultListener()).sendData(protocol);
    }

    @Override
    protected IResultListener<ResultData<String>> getResultListener() {
        return new IResultListener<ResultData<String>>() {
            @Override
            public void onResult(ResultData<String> protocol) {
                // 停止线程检测
                stopCheckTimeOutThread();
                // 调用后续的指令
                if (getRepeaterListener() != null) {
                    getRepeaterListener().onNext();
                    setRepeaterListener(null);
                }
                IMessageListener listener = getMessageListener();
                if (listener != null) {
                    listener.success(protocol.getData());
                }
            }

            @Override
            public void error(String msg) {
                // 停止超时检查线程
                stopCheckTimeOutThread();
                if (getRepeaterListener() != null) {
                    getRepeaterListener().onNext();
                    setRepeaterListener(null);
                }
                IMessageListener messageListener = getMessageListener();
                if (messageListener != null) {
                    messageListener.error(msg, 0);
                }
                setMessageListener(null);
            }
        };
    }

    @Override
    public boolean getRepeater() {
        return false;
    }

    @Override
    protected String getCommandName() {
        return "机械臂命令";
    }
}
