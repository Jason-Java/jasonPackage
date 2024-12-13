package com.jason.jasonPackage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jason.jasontools.commandbus.AbsCommand;
import com.jason.jasontools.commandbus.IMessageListener;
import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.util.JasonThreadPool;
import com.jason.jasontools.util.LogUtil;

import bioyond_robotic.BioyondRoboticCommand;
import bioyond_robotic.BioyondRoboticSocketClient;
import bioyond_robotic.protocol.RoboticRetryProtocol;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BioyondRoboticSocketClient.getInstance().connect("192.168.1.116", 60003, 2000);
        //初始化命令调度中心
        JasonThreadPool.getInstance().execute(CommendExecuteCenterSingle.getInstance());
        findViewById(R.id.menuThree).setOnClickListener(v -> {

            IProtocol protocol = new RoboticRetryProtocol();
//            protocol = new RoboticArmPickOrPlace(1, 1, 1, 1, 1, 1);
            AbsCommand cmd = new BioyondRoboticCommand(new IMessageListener() {
                @Override
                public void start() {
                    LogUtil.i("开始发送数据");
                }

                @Override
                public void success(Object data) {
                    LogUtil.i("成功收到数据");
                }

                @Override
                public void error(String message, int type) {
                    LogUtil.e("发生错误 " + message);
                }
            }, protocol);
            CommendExecuteCenterSingle.getInstance().addQueue(cmd);
        });
    }
}