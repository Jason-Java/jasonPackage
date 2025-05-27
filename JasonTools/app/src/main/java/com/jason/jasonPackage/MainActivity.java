package com.jason.jasonPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jason.jasonuitools.JasonToast;
import com.jason.jasonuitools.util.DensityUtil;
import com.jason.opencv.OpenCameraActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JasonToast.getInstance().init(getApplication(), DensityUtil.sp2px(6));
        findViewById(R.id.menuThree).setOnClickListener(v -> {
            JasonToast.getInstance().makeInfo("正在加载中。。。");
            startActivity(new Intent(MainActivity.this, OpenCameraActivity.class));
        });


    }
}