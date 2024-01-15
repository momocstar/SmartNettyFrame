package com.momoc.netty.frame;

/**
 * @author momoc
 * @version 1.0
 * @className FrameMethodUtils
 * @description
 * @date 2023/7/4 12:44
 */
public class FrameMethodUtils {


    /*
      二进制转byte
    */
    public static byte bit2byte(String bString) {
        byte result = 0;
        for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
            result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }

    /**
     * 一个字节八位，前面要补0够，以湊够消息长度
     *
     * @param msgIdLength
     * @return
     */
    public static byte[] fillBinaryToMsgLength(Integer msgIdLength, int msgId) {
        String msgIdStr = Integer.toBinaryString(msgId);
        //前置补0
        if (msgIdStr.length() < msgIdLength) {
            //填充后
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < msgIdLength - msgIdStr.length(); i++) {
                sb.append(0);
            }
            sb.append(msgIdStr);
            msgIdStr = sb.toString();
        }
        byte[] bytes = new byte[msgIdLength / 8];
        //因为1byte是八字节，除以8
        for (int i = 0; i < msgIdLength / 8; i++) {
            bytes[i] = bit2byte(msgIdStr.substring(8 * i, 8 * (i + 1)));
        }
        return bytes;
    }
}
