package com.jason.instrction_pipeline.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月20日
 */
public interface IClient {


    /**
     * 打开客户端
     */
    IClient openClient() throws IOException;


    /**
     * 获取客户端的输出流
     *
     * @return
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * 获取客户端的输入流
     *
     * @return
     */
    InputStream getInputStream() throws IOException;

    /**
     * 关闭客户端
     */
    void closeClient();

    String getClientTAG();
}
