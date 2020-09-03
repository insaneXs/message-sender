package com.insanexs.message.protocol.modbusTCP;

import com.insanexs.message.MessageSender;
import com.insanexs.message.PooledMessageSenderFactory;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-10-28
 */
public class ModbusTCPMessageSenderFactory implements PooledMessageSenderFactory {
    @Override
    public MessageSender createMessageSender(String protocol, long timeout) {
        return new ModbusTCPMessageSender(protocol, timeout);
    }


    @Override
    public MessageSender createPooledMessageSender(String protocol, long timeout) {
        return null;
    }
}
