package com.jason.jasontools.util;

import android.media.tv.TvView;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月16日
 */
public class StrUtil {
    //手机号正则表达式
    private static final String IS_PHONE = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";
    //邮箱正则表达式
    private static final String IS_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    //提取数字
    private static final String EXTRACTION_OF_DIGITAL = "[^0-9||\\.]";
    //提取字母
    private static final String EXTRACTION_OF_LETTER = "[^a-z||A-Z]";
    //检查是否是数字
    private static final String CHECK_NUMBER = "[\\+-]?[0-9]*(\\.[0-9]*)?([eE][\\+-]?[0-9]+)?";


    /**
     * 判断是否是手机号
     *
     * @param str
     * @return
     */
    public static boolean isPhone(String str) {
        if (str == null) {
            return false;
        }
        if (str.matches(IS_PHONE) == true) {
            return true;
        }
        return false;
    }

    /**
     * 是否是电子邮箱
     *
     * @param str
     * @return {@code true} 如果字符串包含@符号
     */
    public static boolean isEmail(String str) {
        if (str == null) {
            return false;
        }
        if (str.matches(IS_EMAIL) == true) {
            return true;
        }
        return false;
    }


    /**
     * 判断字符串是否为空
     * StringUtil.isEmpty("")   =true
     * StringUtil.isEmpty("    ")   =true
     * StringUtil.isEmpty("asb  ")   =false
     * StringUtil.isEmpty("  abd  ")   =false
     * StringUtil.isEmpty("abc")   =false
     *
     * @param str
     * @return
     */
    public static <T> boolean isEmpty(T str) {
        if (str == null) return true;

        if (str instanceof String) {
            int length;
            String value = (String) str;
            if ((length = value.length()) == 0) return true;

            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 移除字符串串两端的空格
     *
     * @param str 如果字符串为空 则返回空，否者返回移除掉空格的字符串
     * @return
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 从字符串中提取数字
     */
    public static String extractionOfDigital(@NonNull String str) {
        Pattern p = Pattern.compile(EXTRACTION_OF_DIGITAL);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 从字符串中提取字母
     */
    public static String extractionOfLetter(@NonNull String str) {
        Pattern p = Pattern.compile(EXTRACTION_OF_LETTER);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断是否是数值
     *
     * @param str
     * @return
     */
    public static boolean isDigital(@NonNull String str) {
        Pattern p = Pattern.compile(CHECK_NUMBER);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 比较两个字符串是否相等
     * StringUtil.equals(null,null)   =false
     * StringUtil.equals("    ","    ")   =true
     * StringUtil.equals("asb  ","asb")   =false
     * StringUtil.equals("","")   =true
     */
    public static boolean equals(String source, String destination) {
        if (source == null || destination == null) {
            return false;
        }
        if (source.equals(destination)) {
            return true;
        }
        return false;
    }


    /**
     * Hex字符串转成byte数组
     * 如果 字符串为空或者字符串的长度小于1则返回null
     *
     * @param str hex字符串
     * @return byte 数组
     */
    private static byte[] hexStringToBytes(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        int size = str.length() / 2;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; ++i) {
            bytes[i] = (byte) Integer.parseInt(str.substring(i * 2, (i * 2 + 2)), 16);
        }
        return bytes;
    }


    /**
     * hex字符串转ASCLL码字符串
     *
     * @param r hex字符串
     * @return Ascll码字符串
     */
    public final static String byteToASCLLString(String r) {
        StringBuffer bufff = new StringBuffer();
        for (int i = 0; i < r.length(); i += 2) {
            String s = r.substring(i, i + 2);
            if (s.equals("0D")) {
                bufff.append("\n");
            } else {
                int decimal = Integer.parseInt(s, 16);
                bufff.append((char) decimal);
            }
        }
        return bufff.toString();
    }

    /**
     * byte数组转换Hex字符串
     *
     * @param data byte数组
     * @return ASCLL码字符串
     */
    public final static String byteToHexString(byte[] data) {
        StringBuffer bufff = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String s = Integer.toHexString(data[i] & 0xFF);
            if (s.length() < 2) {
                bufff.append(0);
                bufff.append(s);
            } else {
                bufff.append(s);
            }
            bufff.append(" ");
        }
        bufff.delete(bufff.length() - 1, bufff.length());
        return bufff.toString();
    }

    /**
     * byte类型转16进制Hex字符串
     *
     * @param data
     * @return
     */
    public final static String byteToHexString(byte data) {
        String s = Integer.toHexString(data & 0xFF);
        if (s.length() < 2) {
            return "0" + s;
        } else {
            return s;
        }
    }


    /**
     * 字符串Hex转换成Byte数组 <br>
     * ffff0101-->0xff,0xff,0x01,0x01<br>
     * ff ff 01 01--> 0xff,0xff,0x01,0x01
     *
     * @param data
     * @return
     */
    public static byte[] hexToByteArray(String data) {
        if (StrUtil.isEmpty(data)) {
            return null;
        }
        if (data.contains(" ")) {
            data.replace(" ", "");
        }
        if (data.length() % 2 != 0) {
            return null;
        }
        data = data.toUpperCase();
        byte[] bytes = new byte[data.length() / 2];
        char[] chars = data.toCharArray();
        for (int i = 0; i < chars.length; i += 2) {
            byte b = (byte) (charToByte(chars[i]) << 4 | charToByte(chars[i + 1]));
            bytes[i / 2] = b;
        }
        return bytes;
    }


    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * hex数组转换中文字符串
     *
     * @param data
     * @return
     */
    public final static String byteArrayToString(byte[] data) {
        try {
            return new String(data, "GB2312");
        } catch (Exception e) {
            return "";
        }
    }

    public final static String toLowerCaseFirst(String val) {
        if (isEmpty(val)) {
            return val;
        }
        char c = val.charAt(0);
        c = (char) (c + 32);
        return c + val.substring(1, val.length());
    }


    /**
     * 数值进行四舍五入,小数值必须大于等于一位
     *
     * @param value  数值
     * @param length 小数的长度
     */
    public static String decimalRound(String value, int length) {
        if (length > 10) {
            length = 10;
        }
        double dataDouble = 0;
        //先扩大10^length+1次方
        long dataInteger = (long) (Double.parseDouble(value) * Math.pow(10, length + 1));
        //取出最后一位标志位 判断是否大于等于5
        int flag = (int) dataInteger % 10;
        dataInteger = dataInteger / 10;
        if (flag >= 5) {
            dataInteger += 1;
        } else if (flag <= -5) {
            dataInteger -= 1;
        }
        //恢复小数
        dataDouble = dataInteger / Math.pow(10, length);

        //检查小数位数
        String dataString = String.valueOf(dataDouble);
        String integerPart = dataString.split("\\.")[0];
        String decimalPart = dataString.split("\\.")[1];
        int decimalLength = decimalPart.length();
        if (decimalLength >= length) {
            decimalPart = decimalPart.substring(0, length);
        } else {
            for (int i = 0; i < length - decimalLength; i++) {
                decimalPart += "0";
            }
        }
        return integerPart + "." + decimalPart;
    }

    /**
     * 补全位数
     * 补全4位     1---》   0001
     * 补全三位     12---》  012
     *
     * @param number 实际的数字
     * @param bit    补全到多少位
     * @return
     */
    public static String completeInteger(int number, int bit) {
        String value = String.valueOf(number);
        if (value.length() >= bit) {
            return value;
        }
        int dif = bit - value.length();
        for (int i = 0; i < dif; i++) {
            value = "0" + value;
        }
        return value;
    }


    /**
     * String 类型的int值 转化为Int
     * "12"==12
     * ""==0
     * null==0
     * "asf"==0
     * "12asf"==0
     * "asdf12asf"==0
     *
     * @param intValue
     * @return
     */
    public static int toInteger(String intValue) {
        return toDouble(intValue).intValue();
    }

    /**
     * String 类型的Double值 转化为Double值
     * "12"==12d
     * "12.123"==12.123d
     * ""==0d
     * null==0d
     * "asf"==0d
     * "12asf"==0d
     * "asdf12asf"==0d
     *
     * @param doubleValue
     * @return
     */
    public static Double toDouble(String doubleValue) {
        if (isEmpty(doubleValue)) {
            return 0d;
        }
        String value = extractionOfDigital(doubleValue);
        if (value.length() != doubleValue.length()) {
            return 0d;
        }
        return Double.valueOf(doubleValue);
    }

    /**
     * String 类型的Double值 转化为Double值
     * "12"==12f
     * "12.123"==12.123f
     * ""==0f
     * null==0f
     * "asf"==0f
     * "12asf"==0f
     * "asdf12asf"==0f
     *
     * @param floatValue
     * @return
     */
    public static Float toFloat(String floatValue) {
        return toDouble(floatValue).floatValue();
    }

}
