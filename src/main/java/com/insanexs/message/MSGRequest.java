package com.insanexs.message;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-03-12
 */
public interface MSGRequest {

    byte[] serialize();

    long getId();

}
