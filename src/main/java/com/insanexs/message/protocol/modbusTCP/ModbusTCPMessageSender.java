package com.insanexs.message.protocol.modbusTCP;

import com.insanexs.message.MSGRequest;
import com.insanexs.message.MSGResponse;
import com.insanexs.message.MessageSender;
import com.insanexs.message.future.DefaultResponseFuture;
import com.insanexs.message.protocol.modbusTCP.handler.ModbusTCPInboundHandler;
import com.insanexs.message.protocol.modbusTCP.handler.ModbusTCPDecodeHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.util.concurrent.Future;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-07-27
 */

public class ModbusTCPMessageSender implements MessageSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Bootstrap bootstrap;

    private String ip;

    private int port;

    private long timeout;

    private Channel channel;

    public ModbusTCPMessageSender(String ip, int port, long timeout){
        this.ip = ip;
        this.port = port;

        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .option(ChannelOption.SO_REUSEADDR, false)
                .channel(NioSocketChannel.class)
                .handler(new ModbusTCPDecodeHandler())
                .handler(new ModbusTCPInboundHandler());
//        channel = getChannel();

        this.timeout = timeout;
    }

    public ModbusTCPMessageSender(String ip, int port){
        this(ip, port,1000);
    }


    private Channel getChannel(){
        if(channel == null || !channel.isActive()){
            try {
                channel = bootstrap.connect(ip, port).sync().channel();
            } catch (InterruptedException e) {
                logger.warn("Connect Channel Interrupted", e);
            } catch (Exception e){
                logger.warn("Connection failed", e);
            }
        }
        return channel;
    }

    @Override
    public MSGResponse sendCommandSync(MSGRequest reqCmd) {
        MSGResponse resp = null;
        Future<MSGResponse> future = sendCommandAsync(reqCmd);
        if(future != null){
            try {
                resp = future.get();
            } catch (Exception e) {
                logger.warn("Send ResMsg[{}] => {}:{} failed", DatatypeConverter.printHexBinary(reqCmd.serialize()), ip, port);
                logger.warn("And the err is", e);
            }
        }
        return resp;
    }

    @Override
    public Future<MSGResponse> sendCommandAsync(MSGRequest reqCmd) {
        Future<MSGResponse> future = null;
        Channel channel = getChannel();
        if(channel == null){
            logger.warn("could not connect to the {}:{}", ip, port);
            return null;
        }else{
            future = DefaultResponseFuture.sent(channel, reqCmd, timeout);
        }
        return future;
    }
}
