package com.insanexs.message.protocol.modbusTCP.builder;

import com.insanexs.message.protocol.modbusTCP.ModBusTCP;
import com.insanexs.message.util.ByteUtils;

import java.util.Arrays;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-08-23
 */
public class ModbusTCPBuilder {
    private static short transactionIndex = 0;

    public static ModBusTCP buildWriteCommand(byte devId, byte functionCode, int address, int registerCount, byte[] value) {
        ModBusTCP protocol = new ModBusTCP();


        /************Data Partition*****************/
        byte[] registerAddressBytes = ByteUtils.intTo2Bytes(address);

        byte[] dataBytes;
        if(functionCode == 0x06){
            dataBytes = ByteUtils.aggregateBytes(registerAddressBytes, value);
        }else{
            byte[] registerCountBytes = ByteUtils.intTo2Bytes(registerCount);
            byte valueByteSize = ByteUtils.intToByte(value.length);

            dataBytes = ByteUtils.aggregateBytes(registerAddressBytes, registerCountBytes, new byte[]{valueByteSize}, value);
        }


        byte[] dataLength = ByteUtils.intTo2Bytes(dataBytes.length + 2);

        protocol.setTransactionId(generateTransactionId());
        protocol.setProtocolCode(new byte[]{0x00, 0x00});
        protocol.setLength(dataLength);

        protocol.setDevId(devId);
        protocol.setFunctionCode(functionCode);

        protocol.setData(dataBytes);
        return protocol;
    }

    public static ModBusTCP buildWriteCommand(byte functionCode, int address, byte[] value) {
        return buildWriteCommand((byte) 0x01, functionCode, address, 1, value);
    }

    public static ModBusTCP buildReadCommand(byte devId, byte functionCode, int address, int registerCount) {
        ModBusTCP protocol = new ModBusTCP();

        protocol.setTransactionId(generateTransactionId());
        protocol.setProtocolCode(new byte[]{0x00, 0x00});
        protocol.setLength(ByteUtils.intTo2Bytes(6));
        protocol.setDevId(devId);
        protocol.setFunctionCode(functionCode);

        byte[] registerAddr = ByteUtils.intTo2Bytes(address);
        byte[] registerCountBytes = ByteUtils.intTo2Bytes(registerCount);

        byte[] data = ByteUtils.aggregateBytes(registerAddr, registerCountBytes);
        protocol.setData(data);
        return protocol;
    }


    public static ModBusTCP parseFromBytes(byte[] bytes) {
        ModBusTCP protocol = new ModBusTCP();
        protocol.setTransactionId(Arrays.copyOfRange(bytes, 0, 2));
        protocol.setProtocolCode(Arrays.copyOfRange(bytes, 2, 4));
        protocol.setLength(Arrays.copyOfRange(bytes, 4, 6));
        protocol.setDevId(bytes[6]);
        protocol.setFunctionCode(bytes[7]);
        protocol.setData(Arrays.copyOfRange(bytes, 8, bytes.length));

        int length = ByteUtils.bytesArrayToIntBigEndian(protocol.getLength());
        return protocol;
    }

    protected static byte[] generateTransactionId() {
        synchronized (ModbusTCPBuilder.class) {
            transactionIndex = (short) (transactionIndex + 1);
        }
        return ByteUtils.shortToByteArray(transactionIndex);
    }



}
