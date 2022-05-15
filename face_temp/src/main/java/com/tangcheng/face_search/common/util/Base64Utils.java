package com.tangcheng.face_search.common.util;

import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.Base64;

public class Base64Utils {
    public static String inputStream2Base64(InputStream is) throws Exception {
        byte[] data = null;
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                    //System.out.println("关闭流");
                } catch (IOException e) {
                    throw new Exception("输入流关闭异常");
                }
            }
        }

        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * base64转inputStream
     *
     * @param base64string
     * @return
     */
    public static InputStream base2InputStream(String base64string) {
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }
    public static String byteArray2Base(byte[] bytes){
        String base64Str = Base64.getEncoder().encodeToString(bytes);
        return base64Str;
    }
    public static byte[] Base2byteArray(String base64string){
        byte[] decode = Base64.getDecoder().decode(base64string);
        return decode;
    }
    public static void main(String[] args) {
        String file = "D:\\temp\\ed7ecec2-f38c-48b6-b36c-105a0c32aa48\\meinv\\51.jpg";
        File file1 = new File(file);
    }

}
