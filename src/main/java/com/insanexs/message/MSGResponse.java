package com.insanexs.message;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-03-12
 */
public interface MSGResponse {
    byte[] serialize();

    long getId();

    boolean isErrFrame();

    byte[] getValue();
}
