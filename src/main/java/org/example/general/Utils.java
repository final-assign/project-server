package org.example.general;

import java.nio.charset.StandardCharsets;

public class Utils {
    public static byte[] intToBytes(int data) {
        return new byte[]{
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    public static int bytesToInt(byte[] data, int start) {
        return (int) (
                (0xff & data[start]) << 24 |
                        (0xff & data[start + 1]) << 16 |
                        (0xff & data[start + 2]) << 8 |
                        (0xff & data[start + 3]) << 0
        );
    }

    public static byte[] shortToBytes(int data) {
        return new byte[]{
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    public static short bytesToShort(byte[] data, int start) {
        return (short) (
                (0xff & data[start]) << 8 |
                        (0xff & data[start + 1]) << 0
        );
    }

    public static byte[] longToBytes(long data) {
        return new byte[]{
                (byte) ((data >> 56) & 0xff),
                (byte) ((data >> 48) & 0xff),
                (byte) ((data >> 40) & 0xff),
                (byte) ((data >> 32) & 0xff),
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    public static long bytesToLong(byte[] data, int start) {
        return (long) (
                (long) (0xff & data[start]) << 56 |
                        (long) (0xff & data[start + 1]) << 48 |
                        (long) (0xff & data[start + 2]) << 40 |
                        (long) (0xff & data[start + 3]) << 32 |
                        (long) (0xff & data[start + 4]) << 24 |
                        (long) (0xff & data[start + 5]) << 16 |
                        (long) (0xff & data[start + 6]) << 8 |
                        (long) (0xff & data[start + 7]) << 0
        );
    }

    //문자열 길이 구하는 함수
    public static int getByteLength(String str) {
        return (str == null) ? 0 : str.getBytes(StandardCharsets.UTF_8).length;
    }

    //배열에 문자열 쓰는 함수
    public static int writeString(byte[] dest, int cursor, String str) {
        if (str == null) str = "";

        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);//utf8 체크용

        //문자열 길이
        System.arraycopy(Utils.intToBytes(bytes.length), 0, dest, cursor, 4);
        cursor += 4;
        //문자열
        System.arraycopy(bytes, 0, dest, cursor, bytes.length);
        cursor += bytes.length;

        return cursor;
    }
}