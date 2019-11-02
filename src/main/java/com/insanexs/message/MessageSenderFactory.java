package com.insanexs.message;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-10-25
 */
public interface MessageSenderFactory {

    MessageSender createMessageSender(String protocol, int timeout);

}
