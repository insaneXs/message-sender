package com.insanexs.message.protocol.modbusTCP;

import com.insanexs.message.MessageSender;
import com.insanexs.message.MessageSenderFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: xieshang
 * @Description:
 * @Date: Create at 2019-10-28
 */
public class ModbusTCPMessageSenderFactory implements MessageSenderFactory {

    private static Pattern pattern = Pattern.compile("[modbusTCP://]+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");


    @Override
    public MessageSender createMessageSender(String protocol, int timeout) {
        Matcher matcher = pattern.matcher(protocol);
        if(StringUtils.isEmpty(protocol)){
            throw new IllegalArgumentException("protocol can not be null or empty");
        }else if(!matcher.matches()){
            throw new IllegalArgumentException("illegal protocol format:" + protocol);
        }/*else if(!matcher.find()){
            throw new IllegalArgumentException("can not find ip or port");
        }*/


        String ip = matcher.group(1);
        String port = matcher.group(2);

        return doCreate(ip, Integer.parseInt(port), timeout);
    }

    private MessageSender doCreate(String ip, int port, long timeout){
        return new ModbusTCPMessageSender(ip, port, timeout);

    }

}
