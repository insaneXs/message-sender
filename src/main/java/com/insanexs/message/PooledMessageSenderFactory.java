package com.insanexs.message;

/**
 * @ClassName PooledMessageSenderFactory
 * @Description Pooled-MessageSender-Factory
 * @Author insaneXs
 * @Date 2020/8/20
 */
public interface PooledMessageSenderFactory extends MessageSenderFactory{

    MessageSender createPooledMessageSender(String protocol, int timeout);
}
