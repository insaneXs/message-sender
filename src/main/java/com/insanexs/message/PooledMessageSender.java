package com.insanexs.message;

/**
 * @ClassName PooledMessageSender
 * @Description TODO
 * @Author insaneXs
 * @Date 2020/8/20
 */
public abstract class PooledMessageSender implements MessageSender{

    protected abstract MessageSender getMessageSenderFromPool(String protocol, long timeout);

    protected abstract void refund();

    protected class MessageSenderHolder<T extends MessageSender>{
        private T messageSender;
        private long lru;

        public MessageSenderHolder(T messageSender, long lru){
            this.messageSender = messageSender;
            this.lru = lru;
        }

        public T getMessageSender(){
            return messageSender;
        }

        public long getLru(){
            return lru;
        }
    }
}
