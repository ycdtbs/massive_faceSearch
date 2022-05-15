package com.tangcheng.face_search.common.util;

import io.swagger.models.auth.In;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteUtils {
    public static List<Float> byteArray2List(byte[] bytes){
        List<Float> floats = new ArrayList<>();
        for (int i = 7; i < bytes.length - 4 ; i+=4) {
            byte[] tempByteList = new byte[4];
            tempByteList[0] = bytes[i];
            tempByteList[0] = bytes[i + 1];
            tempByteList[0] = bytes[i + 2];
            tempByteList[0] = bytes[i + 3];
            tempByteList[0] = bytes[i + 4];
            float v = byte2float(tempByteList, 0);
            floats.add(v);
        }
        return floats;
    }
    //这个函数将float转换成byte[]
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    public static List<Long> String2IntArray(String str){
        long i = Long.parseLong(str);
        List<Long> integers = new ArrayList<>();
        integers.add(i);
        return integers;
    }


    //这个函数将byte转换成float
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }
    // 这个函数将 字符串转为int
    public static List<Integer> long2ListInteager(String str)  {
        int[] ints = Arrays.asList(str).stream().mapToInt(Integer::parseInt).toArray();
        List<Integer> integers = new ArrayList<>();
        for (int i:ints
             ) {
            integers.add(i);
        }
        return integers;
    }
    public static byte[] intToBytes( int value ) {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    public static void main(String[] args) throws IOException {

    }
}
