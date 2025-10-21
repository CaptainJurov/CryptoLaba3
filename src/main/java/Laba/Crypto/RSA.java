package Laba.Crypto;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class RSA {
    private static final Random random = new Random();

    public static class KeyPair implements Serializable {
        public final BigInteger privateKey; // d
        public final BigInteger publicKey;  // (n, e)
        public final BigInteger n;
        public final BigInteger e;

        public KeyPair(BigInteger privateKey, BigInteger publicKey, BigInteger n, BigInteger e) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.n = n;
            this.e = e;
        }
    }

    public static class Signature implements Serializable {
        public final BigInteger signature;

        public Signature(BigInteger signature) {
            this.signature = signature;
        }
    }

    // Генерация ключевой пары
    public KeyPair generateKeyPair() {
        BigInteger p = BigInteger.probablePrime(512, random);
        BigInteger q = BigInteger.probablePrime(512, random);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = new BigInteger("65537");
        BigInteger d = e.modInverse(phi);

        return new KeyPair(d, n, n, e);
    }

    // Подписание файла
    public Signature signFile(File file, KeyPair keyPair) throws Exception {
        byte[] fileData = java.nio.file.Files.readAllBytes(file.toPath());
        BigInteger hash = new BigInteger(1, hash(fileData));
        BigInteger signature = hash.modPow(keyPair.privateKey, keyPair.n);
        return new Signature(signature);
    }

    // Проверка подписи
    public boolean verifyFile(File file, Signature signature, BigInteger publicKey, BigInteger e) throws Exception {
        byte[] fileData = java.nio.file.Files.readAllBytes(file.toPath());
        BigInteger hash = new BigInteger(1, hash(fileData));
        BigInteger decrypted = signature.signature.modPow(e, publicKey);
        return hash.equals(decrypted);
    }

    private byte[] hash(byte[] message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(message);
    }
}