package com.jason.opencv;

import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.widget.UVCCameraTextureView;


public class OpenCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);
        UVCCamera uvcCamera = new UVCCamera();
        TextureView view = findViewById(R.id.AAA);
        USBMonitor usbMonitor=new USBMonitor(this, new USBMonitor.OnDeviceConnectListener() {
            @Override
            public void onAttach(UsbDevice device) {

            }

            @Override
            public void onDettach(UsbDevice device) {

            }

            @Override
            public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
                uvcCamera.open(ctrlBlock);
                uvcCamera.setPreviewTexture(view.getSurfaceTexture());
                uvcCamera.startPreview();

            }

            @Override
            public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {

            }

            @Override
            public void onCancel(UsbDevice device) {

            }
        });

    }
}