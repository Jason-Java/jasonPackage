package com.jason.jasontools.util;


import com.jason.jasontools.commandbus.IProtocol;

/**
 * <table>
 * <tr>
 *     <th>功能码</th><th>含义</th><th>操作类型</th><th>操作数量</th>
 * </tr>
 * <tr>
 *     <th>0x01</th><th>读线圈状态</th><th>位操作</th><th>单个或多个</th>
 * </tr>
 *
 * <tr>
 *     <th>0x02</th><th>读离散输入状态</th><th>位操作</th><th>单个或多个</th>
 * </tr>
 *
 * <tr>
 *     <th>0x03</th><th>读保持寄存器</th><th>字操作</th><th>单个或多个</th>
 * </tr>
 *
 * <tr>
 *     <th>0x04</th><th>读输入寄存器</th><th>字操作</th><th>单个或多个</th>
 * </tr>
 *
 * <tr>
 * <tr>
 *     <th>0x05</th><th>写线圈状态</th><th>位操作</th><th>单个</th>
 * </tr>
 *     <th>0x06</th><th>写单个保持寄存器</th><th>字操作</th><th>单个</th>
 * </tr>
 *
 * <tr>
 *     <th>0x0F</th><th>写多个线圈</th><th>位操作</th><th>多个</th>
 * </tr>
 *
 * <tr>
 *     <th>0x10</th><th>写多个保持寄存器</th><th>字操作</th><th>多个</th>
 * </tr>
 * </table>
 *
 *
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年08月06日
 */
public class ModbusTCPUtil {

    public static synchronized void  readMultipleCoil(int address, short readLen) {

    }
}
