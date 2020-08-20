package com.insanexs.message.protocol.modbusTCP;

import com.insanexs.message.MessageSender;
import com.insanexs.message.MessageSenderFactory;
import com.insanexs.message.PooledMessageSenderFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-10-28
 */
public class ModbusTCPMessageSenderFactory implements PooledMessageSenderFactory {
    @Override
    public MessageSender createMessageSender(String protocol, int timeout) {
        return new ModbusTCPMessageSender(protocol, timeout);
    }


    @Override
    public MessageSender createPooledMessageSender(String protocol, int timeout) {
        return null;
    }
}
