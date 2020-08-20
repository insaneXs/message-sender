package com.insanexs.message.protocol.modbusTCP;

import com.insanexs.message.MSGRequest;
import com.insanexs.message.MSGResponse;
import com.insanexs.message.MessageSender;
import com.insanexs.message.PooledMessageSender;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @ClassName PooledModbusTCPMessageSender
 * @Description TODO
 * @Author insaneXs
 * @Date 2020/8/20
 */
public class PooledModbusTCPMessageSender extends PooledMessageSender {

    private static Map<String, List<MessageSenderHolder<ModbusTCPMessageSender>>> pool = new HashMap<>();

    private ModbusTCPMessageSender target;

    private String protocol;

    private static long maxIdleTime = 60 * 1000 * 30;

    static {
        new Thread(()->{
            while(true){
                try{
                    synchronized (PooledModbusTCPMessageSender.class){
                        for(List<MessageSenderHolder<ModbusTCPMessageSender>> queue : pool.values()){
                            for(MessageSenderHolder holder : queue){
                                if(System.currentTimeMillis() - holder.getLru() > maxIdleTime){
                                    holder.getMessageSender().close();
                                    queue.remove(holder);
                                    holder = null;
                                }
                            }
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "ModbusTCPMessageSenderPoolCleaner").start();
    }

    public PooledModbusTCPMessageSender(String protocol, long timeout){
        this.protocol = protocol;
        getMessageSenderFromPool(protocol, timeout);
    }

    @Override
    protected MessageSender getMessageSenderFromPool(String protocol, long timeout) {
        synchronized (PooledModbusTCPMessageSender.class){
            List<MessageSenderHolder<ModbusTCPMessageSender>> queue = pool.get(protocol);
            if(queue == null){
                target = new ModbusTCPMessageSender(protocol, timeout);
            }else{
                target = queue.remove(queue.size() - 1).getMessageSender();
                target.setTimeout(timeout);
                if(queue.size() == 1){
                    pool.remove(protocol);
                }
            }
            return target;
        }
    }


    @Override
    protected void refund() {
        synchronized (PooledModbusTCPMessageSender.class){
            List<MessageSenderHolder<ModbusTCPMessageSender>> queue = pool.get(protocol);
            if(queue == null){
                queue = new LinkedList<>();
            }
            queue.add(new MessageSenderHolder<>(target, System.currentTimeMillis()));
            pool.put(protocol, queue);
            target = null;
        }
    }

    @Override
    public MSGResponse sendCommandSync(MSGRequest reqCmd) {
        check();
        return target.sendCommandSync(reqCmd);
    }

    @Override
    public Future<MSGResponse> sendCommandAsync(MSGRequest reqCmd) {
        check();
        return target.sendCommandAsync(reqCmd);
    }

    private void check(){
        if(target == null){
            throw new IllegalStateException("MessageSender Can not be reused after closed");
        }
    }

    @Override
    public void close() {
        if(target != null){
            refund();
        }
    }
}
