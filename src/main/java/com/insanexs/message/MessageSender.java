package com.insanexs.message;

import java.util.concurrent.Future;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-10-25
 */
public interface MessageSender {
    /**
     * 同步发送请求
     * @param reqCmd
     * @return
     */
    MSGResponse sendCommandSync(MSGRequest reqCmd);

    /**
     * 异步发送请求
     * @param reqCmd
     * @return
     */
    Future<MSGResponse> sendCommandAsync(MSGRequest reqCmd);

    /**
     * 关闭MessageSender
     */
    void close();
}
