package com.insanexs.message.util;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-08-23
 */
public class ByteUtils {
    public static byte[] intTo2Bytes(int value) {
        return shortToByteArray((short) value);
    }

    public static byte intToByte(int value) {
        return (byte) value;
    }

    public static byte[] shortToByteArray(short value) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (value);
        bytes[0] = (byte) (value >> 8);
        return bytes;
    }

    public static int bytesArrayToIntBigEndian(byte[] bytes) {
        int val;
        switch (bytes.length) {
            case 1:
                val = bytes[0];
                break;
            case 2:
                val = (bytes[0] << 8) + bytes[1];
                break;
            case 3:
                val = (bytes[0] << 16) + (bytes[1] << 8) + bytes[2];
                break;
            default:
                val = (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
        }
        return val;
    }

    public static byte[] aggregateBytes(byte[]... byteArrays) {
        int length = 0;
        for (byte[] bytes : byteArrays) {
            length += bytes.length;
        }
        byte[] aggregatedBytes = new byte[length];

        int index = 0;
        for (byte[] bytes : byteArrays) {
            System.arraycopy(bytes, 0, aggregatedBytes, index, bytes.length);
            index += bytes.length;
        }
        return aggregatedBytes;
    }
}
