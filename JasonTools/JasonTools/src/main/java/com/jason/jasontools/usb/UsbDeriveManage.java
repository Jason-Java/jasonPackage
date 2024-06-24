package com.jason.jasontools.usb;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;


import com.jason.jasontools.util.LogUtil;
import com.jason.jasontools.util.StrUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * <p>
 * 描述: Usb驱动管理类
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年07月20日
 */
public class UsbDeriveManage {

    private UsbManager usbManager = null;

    private HashMap<String, UsbDevice> usbDeviceMap = null;
    private HashMap<String, UsbInterface> usbInterfaceMap = null;
    private HashMap<String, UsbEndpoint> usbEndpointInMap = null;
    private HashMap<String, UsbEndpoint> usbEndpointOutMap = null;

    private UsbDeriveManage() {
        usbDeviceMap = new HashMap<>();
        usbInterfaceMap = new HashMap<>();
        usbEndpointInMap = new HashMap<>();
        usbEndpointOutMap = new HashMap<>();
    }

    private static class Singler {
        private static UsbDeriveManage usbDerive = new UsbDeriveManage();
    }

    public static UsbDeriveManage getInstance() {
        return Singler.usbDerive;
    }


    /**
     * 初始化打印机设备
     *
     * @param usbManager
     */
    public void init(UsbManager usbManager) {
        this.usbManager = usbManager;
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        //发现打印机
        for (String key : deviceList.keySet()) {
            if (deviceList.get(key).getVendorId() == 1409) {
                usbDeviceMap.put("1409", deviceList.get(key));
            }
            // todo 添加其他指定设备的打印机
        }
        if (usbDeviceMap.size() == 0) {
            System.err.println("未发现设备");
            return;
        }
        // 获取打印机的接口
        for (String key : usbDeviceMap.keySet()) {
            UsbInterface usbInterface = usbDeviceMap.get(key).getInterface(0);
            usbInterfaceMap.put(key, usbInterface);
            //获取Usb输入端口
            UsbEndpoint endpoint = usbInterface.getEndpoint(0);
            if (endpoint != null && endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                usbEndpointInMap.put(key, endpoint);
            }
            //获取Usb输出端口
            if (usbInterface.getEndpointCount() == 2) {
                endpoint = usbInterface.getEndpoint(1);
                if (endpoint != null && endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    usbEndpointOutMap.put(key, endpoint);
                }
            }
        }
    }

    /**
     * 发送指令
     *
     * @param usbID 打印机品牌
     * @param cmd   命令
     * @return
     */
    public int sendCmd(String usbID, byte[] cmd) {
        UsbDevice usbDevice = usbDeviceMap.get(usbID);
        if (!usbManager.hasPermission(usbDevice)) {
            System.out.println("未获取权限");
            return -1;
        }
        UsbDeviceConnection usbDeviceConnection = usbManager.openDevice(usbDevice);
        if (usbDeviceConnection == null || !usbDeviceConnection.claimInterface(usbInterfaceMap.get(usbID), true)) {
            return -1;
        }
        int redFlag = 0;
        synchronized (usbDevice) {
            try {
                redFlag = usbDeviceConnection.bulkTransfer(usbEndpointInMap.get(usbID), cmd, cmd.length, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                usbDeviceConnection.close();
            }
            return redFlag;
        }
    }

    /*private void readCmd(String usbID) {
        UsbDevice usbDevice = usbDeviceMap.get(usbID);
        if (!usbManager.hasPermission(usbDevice)) {
            System.out.println("未获取权限");
            return;
        }
        UsbDeviceConnection usbDeviceConnection = usbManager.openDevice(usbDevice);
        if (usbDeviceConnection == null || !usbDeviceConnection.claimInterface(usbInterfaceMap.get(usbID), true)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int max = usbEndpointInMap.get(usbID).getMaxPacketSize();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(max);
                    UsbRequest usbRequest = new UsbRequest();
                    usbRequest.initialize(usbDeviceConnection, usbEndpointInMap.get(usbID));
                    usbRequest.queue(byteBuffer, max);
//                int redFlag = usbDeviceConnection.bulkTransfer(usbEndpointInMap.get(usbID), bytes, max, 1000);
                    LogUtil.i(byteBuffer.toString());
                }
            }
        }).start();

    }
*/

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void findUsbDevice(UsbManager usbManager) {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        //发现打印机
        for (String key : deviceList.keySet()) {
            System.out.println("设备 ProductId " + deviceList.get(key).getProductId());
            System.out.println("设备 vendorId " + deviceList.get(key).getVendorId());
        }
    }

}
