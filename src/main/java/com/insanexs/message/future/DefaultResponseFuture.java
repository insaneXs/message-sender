package com.insanexs.message.future;

import com.insanexs.message.MSGRequest;
import com.insanexs.message.MSGResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-03-12
 */
public class DefaultResponseFuture implements Future<MSGResponse> {

    private static Map<Long, DefaultResponseFuture> FUTURES = new ConcurrentHashMap<>();

    private MSGRequest request;
    private MSGResponse response;

    private Channel channel;

    private long id;

    private final long start = System.currentTimeMillis();

    private long timeout = 1000;

    private static Logger logger = LoggerFactory.getLogger(DefaultResponseFuture.class);

    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();

    static{
        Thread thread = new Thread(new TimeoutScan());
        thread.setDaemon(false);
        thread.start();
    }

    public DefaultResponseFuture(Channel channel, MSGRequest request){
        this(channel,request, 1000);
    }

    public DefaultResponseFuture(Channel channel, MSGRequest request, long timeout){
        this.request = request;
        this.id = request.getId();
        this.channel = channel;

        this.timeout = timeout > 0 ? timeout : 1000;

        FUTURES.put(id, this);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return response != null;
    }

    public MSGResponse get() throws InterruptedException, ExecutionException {
        try {
            return get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MSGResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (timeout <= 0) {
            timeout = 1000;
        }

        lock.lock();
        try {
            while (!isDone()) {
                if (!done.await(timeout, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
            if(channel != null){
                channel.close().sync();
            }
        }
        return response;
    }

    protected long getStartTimestamp(){
        return start;
    }

    public long getTimeout(){
        return timeout;
    }

    protected Channel getChannel() {
        return channel;
    }

    protected void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static Future sent(Channel channel, MSGRequest request) {
        return sent(channel, request, 1000);
    }

    public static Future sent(Channel channel, MSGRequest request, long timeout) {
        DefaultResponseFuture future = FUTURES.get(request.getId());
        if (future == null) {
            future = new DefaultResponseFuture(channel, request, timeout);
            future.doSent(channel, request);
        }
        return future;
    }

    private void doSent(Channel channel, MSGRequest request){
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(request.serialize());
        channel.writeAndFlush(buf).addListener((ChannelFuture future)-> {
            if(future.isSuccess()){
                logger.info("Send Message:" + DatatypeConverter.printHexBinary(request.serialize()) + "; success");
            }else{
                logger.warn("Send Message:" + DatatypeConverter.printHexBinary(request.serialize()) + "; error!!!");
            }
        });
    }

    private void doReceived(MSGResponse res) {
        lock.lock();
        try {
            response = res;
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void received(Channel channel, MSGResponse response) throws InterruptedException {
        if (response == null || FUTURES.get(response.getId()) == null) {
            logger.warn("The timeout response finally returned at "
                    + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                    + ", response " + response
                    + (channel == null ? "" : ", channel: " + channel.localAddress()
                    + " -> " + channel.remoteAddress()));
        } else {
            DefaultResponseFuture future = FUTURES.remove(response.getId());
            if(future != null){
                future.doReceived(response);
            }
            if(channel != null){
                channel.close().sync();
            }

        }
    }


    private static class TimeoutScan implements Runnable{

        public void run() {
            while (true) {
                try {
                    for (DefaultResponseFuture future : FUTURES.values()) {
                        if (future == null || future.isDone()) {
                            continue;
                        }
                        if (System.currentTimeMillis() - future.getStartTimestamp() > future.getTimeout()) {
                            byte[] data = future.request.serialize();
                            logger.warn("Request data[" + DatatypeConverter.printHexBinary(data) + "] time out");
                            // handle response.
                            FUTURES.remove(future.request.getId());
                            DefaultResponseFuture.received(future.getChannel(), null);
                        }
                    }
                    Thread.sleep(100);
                } catch (Throwable e) {
                    logger.error("Exception when scan the timeout invocation of remoting.", e);
                }
            }
        }
    }
}
