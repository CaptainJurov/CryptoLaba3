package Laba.Crypto;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class GOST {
    private static final Random random = new Random();

    public static class SystemParams implements Serializable {
        public final BigInteger p;
        public final BigInteger q;
        public final BigInteger a;

        public SystemParams(BigInteger p, BigInteger q, BigInteger a) {
            this.p = p;
            this.q = q;
            this.a = a;
        }
    }

    public static class KeyPair implements Serializable {
        public final BigInteger privateKey;
        public final BigInteger publicKey;
        public final SystemParams params;

        public KeyPair(BigInteger privateKey, BigInteger publicKey, SystemParams params) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.params = params;
        }
    }

    public static class Signature implements Serializable {
        public final BigInteger r;
        public final BigInteger s;

        public Signature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }
    }

    // Генерация параметров системы
    public static SystemParams generateSystemParams() {
        BigInteger q = BigInteger.probablePrime(256, random);
        BigInteger p = q.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);

        while (!p.isProbablePrime(100)) {
            q = BigInteger.probablePrime(256, random);
            p = q.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);
        }

        BigInteger a = new BigInteger("2");
        return new SystemParams(p, q, a);
    }

    // Генерация ключевой пары с заданными параметрами
    public KeyPair generateKeyPair(SystemParams params) {
        BigInteger privateKey = new BigInteger(255, random);
        BigInteger publicKey = params.a.modPow(privateKey, params.p);
        return new KeyPair(privateKey, publicKey, params);
    }

    // Подписание файла
    public Signature signFile(File file, KeyPair keyPair) throws Exception {
        byte[] fileData = java.nio.file.Files.readAllBytes(file.toPath());
        BigInteger hash = new BigInteger(1, hash(fileData)).mod(keyPair.params.q);

        BigInteger k, r, s;

        do {
            k = new BigInteger(keyPair.params.q.bitLength() - 1, random);
            r = keyPair.params.a.modPow(k, keyPair.params.p).mod(keyPair.params.q);
            s = k.multiply(hash).add(keyPair.privateKey.multiply(r)).mod(keyPair.params.q);
        } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));

        return new Signature(r, s);
    }

    // Проверка подписи
    public boolean verifyFile(File file, Signature signature, KeyPair keyPair) throws Exception {
        return verifyFile(file, signature, keyPair.publicKey, keyPair.params);
    }

    public boolean verifyFile(File file, Signature signature, BigInteger publicKey, SystemParams params) throws Exception {
        byte[] fileData = java.nio.file.Files.readAllBytes(file.toPath());
        BigInteger hash = new BigInteger(1, hash(fileData)).mod(params.q);

        if (signature.r.compareTo(BigInteger.ZERO) <= 0 ||
                signature.r.compareTo(params.q) >= 0 ||
                signature.s.compareTo(BigInteger.ZERO) <= 0 ||
                signature.s.compareTo(params.q) >= 0) {
            return false;
        }

        BigInteger v = hash.modPow(params.q.subtract(BigInteger.valueOf(2)), params.q);
        BigInteger z1 = signature.s.multiply(v).mod(params.q);
        BigInteger z2 = params.q.subtract(signature.r).multiply(v).mod(params.q);
        BigInteger u = params.a.modPow(z1, params.p)
                .multiply(publicKey.modPow(z2, params.p))
                .mod(params.p)
                .mod(params.q);

        return u.equals(signature.r);
    }

    private byte[] hash(byte[] message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(message);
    }
}