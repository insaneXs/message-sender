package com.insanexs.message.protocol.modbusTCP.handler;

import com.insanexs.message.future.DefaultResponseFuture;
import com.insanexs.message.protocol.modbusTCP.ModBusTCP;
import com.insanexs.message.protocol.modbusTCP.builder.ModbusTCPBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;


/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-07-17
 */
@ChannelHandler.Sharable
public class ModbusTCPInboundHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        try {

            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);

            logger.info("Receive message:" + DatatypeConverter.printHexBinary(data));

            ModBusTCP response = ModbusTCPBuilder.parseFromBytes(data);

            DefaultResponseFuture.received(ctx.channel(), response);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
