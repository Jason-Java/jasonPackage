package com.jason.jasontools.util;


/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月22日
 */
public class JasonVerifyUtil {
    private static final String TAG = "CrcVerifyTAG";

    public static byte crc8_Maxim(byte[] bytes, int offset, int len){
        int wCRCin = 0x00;
        int wCPoly = 0x8C;
        for (int i = offset; i < offset+len; i++) {
            wCRCin ^= ((long) bytes[i] & 0xFF);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x01) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        wCRCin ^= 0x00;
        LogUtil.e(TAG, "crc = " + StrUtil.byteToHexString((byte) wCRCin));
        return (byte) wCRCin;
    }

    /**
     * bcc算法 对每一个字节进行异或运算最终得到一个8位的验证码
     * @param bytes 待验证的byte数组
     * @param offset 开始验证字节的偏移量 偏移量从0开始
     * @param len 待验证字节的长度
     * @return
     */
    public static byte bcc(byte[] bytes, int offset, int len) {
        offset= Math.max(offset, 0);
        offset = Math.min(offset, bytes.length - 1);
        byte bcc = bytes[offset];
        for (int i = offset + 1; i < offset + len && i < bytes.length; i++) {
            bcc ^= bytes[i];
        }
        return bcc;
    }

    /**
     * CRC校验算法
     *
     * @param bytes
     * @return
     */
  public static   byte[] crc16_Modbus(byte[] bytes) {
        int CRC = 0x0000FFFF;
        int POLYNOMIAL = 0x0000A001;

        for (int i = 0; i < bytes.length; ++i) {
            CRC ^= bytes[i] & 255;

            for (int j = 0; j < 8; ++j) {
                if ((CRC & 1) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        byte[] data = new byte[2];
        // 获取第八位
        data[0] = (byte) (CRC & 0x000000ff);
        // 获取 高八位
        data[1] = (byte) ((CRC & 0x0000ff00)>>8);
        return data;
    }

}
