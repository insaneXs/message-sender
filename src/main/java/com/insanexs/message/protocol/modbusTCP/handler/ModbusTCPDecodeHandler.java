package com.insanexs.message.protocol.modbusTCP.handler;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-09-16
 */
public class ModbusTCPDecodeHandler extends LengthFieldBasedFrameDecoder {

    public ModbusTCPDecodeHandler(){
        super(1024,4,2,0, 6);
    }
}
