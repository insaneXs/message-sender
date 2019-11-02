package com.insanexs.message.protocol.modbusTCP;

import com.insanexs.message.MSGRequest;
import com.insanexs.message.MSGResponse;
import com.insanexs.message.util.ByteUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-07-27
 */
public class ModBusTCP implements MSGRequest, MSGResponse {
    private byte[] transactionId;

    private byte[] protocolCode;

    private byte[] length;

    private byte devId;

    private byte functionCode;

    private byte[] data;

    public byte[] getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(byte[] transactionId) {
        this.transactionId = transactionId;
    }

    public byte[] getProtocolCode() {
        return protocolCode;
    }

    public byte[] getLength() {
        return length;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public byte getDevId() {
        return devId;
    }

    public void setDevId(byte devId) {
        this.devId = devId;
    }

    public byte getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(byte functionCode) {
        this.functionCode = functionCode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setProtocolCode(byte[] protocolCode) {
        this.protocolCode = protocolCode;
    }

    public byte[] serialize(){
        return ByteUtils.aggregateBytes(transactionId, protocolCode, length, new byte[]{devId}, new byte[]{functionCode},
                data);
    }

    @Override
    public byte[] getValue() {
        if(isErrFrame())
            return null;
        return Arrays.copyOfRange(data, 1, data.length);
    }

    @Override
    public long getId() {
        return ByteUtils.bytesArrayToIntBigEndian(transactionId);
    }

    @Override
    public String toString() {
        return "ModBusTCP[" + DatatypeConverter.printHexBinary(serialize()) +
                "]";
    }

    @Override
    public boolean isErrFrame() {
        return functionCode > 0x80;
    }
}
