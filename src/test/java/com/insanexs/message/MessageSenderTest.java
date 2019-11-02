package com.insanexs.message;

import com.insanexs.message.util.ByteUtils;
import com.insanexs.message.protocol.modbusTCP.ModbusTCPMessageSenderFactory;
import com.insanexs.message.protocol.modbusTCP.builder.ModbusTCPBuilder;

import javax.xml.bind.DatatypeConverter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-09-10
 */
public class MessageSenderTest {
    public static void main(String[] args){
        String protocol = "modbusTCP://127.0.0.1:502";
        MessageSenderFactory factory = new ModbusTCPMessageSenderFactory();
        MessageSender sender = factory.createMessageSender(protocol, 1000);
        Future<MSGResponse> future = sender.sendCommandAsync(ModbusTCPBuilder.buildWriteCommand((byte)0x06, 02, ByteUtils.intTo2Bytes(1)));
        try {
            MSGResponse response = future.get();
            System.out.println(DatatypeConverter.printHexBinary(response.serialize()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
