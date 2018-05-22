package org.cfpa.i18nupdatemod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashChecker {
    private static MessageDigest md5;

    public static void init() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("md5");
    }

    public static boolean checkMD5(File fileIn, String expected) throws IOException {
        String md5;
        try {
            md5 = md5HashCode(fileIn);
        } catch (FileNotFoundException e) {
            return false;
        }
        return md5.equals(expected);
    }

    public static String md5HashCode(File fileIn) throws IOException {
        FileInputStream fis = new FileInputStream(fileIn);
        byte[] buffer = new byte[1024];
        int length = -1;
        while ((length = fis.read(buffer, 0, 1024)) != -1) {
            md5.update(buffer, 0, length);
        }
        fis.close();
        byte[] md5Bytes = md5.digest();
        BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
        return bigInt.toString(16);
    }
}