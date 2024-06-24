package com.jason.jasonPackage;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.jason.jasonuitools.view.ShadowDrawable;

import java.util.concurrent.CountDownLatch;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* Drawable drawable = new ShadowDrawable.Builder()
                .setBgColor(Color.parseColor("#1559A0"))
                .setShadowColor(Color.parseColor("#AA1559A0"))
                .setShadowRadius(10)
                .setOffsetBottom(10)
                .setOffsetRight(-10)
                .setRadius(50)
                .builder();
        viewById.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(viewById, drawable);*/
    }
}