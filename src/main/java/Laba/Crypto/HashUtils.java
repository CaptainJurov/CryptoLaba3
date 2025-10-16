package Laba.Crypto;

import java.security.MessageDigest;

public class HashUtils {
    public static byte[] computeSHA256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }
    public static byte[] computeMD5(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return digest.digest(data);
    }

    public static byte[] computeGOST3411(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("GOST3411", "BC");
        return digest.digest(data);
    }
}