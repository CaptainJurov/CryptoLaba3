package Laba.Crypto;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class ElGamal {
    private static final Random random = new Random();

    public static class SystemParams implements Serializable {
        public final BigInteger p;
        public final BigInteger g;

        public SystemParams(BigInteger p, BigInteger g) {
            this.p = p;
            this.g = g;
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
        // Используем надежное простое число
        BigInteger p = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639319");
        BigInteger g = new BigInteger("2");
        return new SystemParams(p, g);
    }

    // Генерация ключевой пары с заданными параметрами
    public KeyPair generateKeyPair(SystemParams params) {
        // Приватный ключ должен быть в диапазоне [2, p-2]
        BigInteger privateKey = new BigInteger(params.p.bitLength() - 2, random)
                .add(BigInteger.ONE);
        BigInteger publicKey = params.g.modPow(privateKey, params.p);
        return new KeyPair(privateKey, publicKey, params);
    }

    // Подписание файла
    public Signature signFile(File file, KeyPair keyPair) throws Exception {
        byte[] fileData = java.nio.file.Files.readAllBytes(file.toPath());
        BigInteger hash = new BigInteger(1, hash(fileData));

        BigInteger k, r, s;
        BigInteger pMinusOne = keyPair.params.p.subtract(BigInteger.ONE);

        do {
            // k должно быть взаимно просто с p-1
            do {
                k = new BigInteger(keyPair.params.p.bitLength() - 2, random)
                        .add(BigInteger.ONE);
            } while (!k.gcd(pMinusOne).equals(BigInteger.ONE));

            r = keyPair.params.g.modPow(k, keyPair.params.p);

            // s = (hash - privateKey * r) * k^(-1) mod (p-1)
            BigInteger kInv = k.modInverse(pMinusOne);
            s = hash.subtract(keyPair.privateKey.multiply(r))
                    .multiply(kInv)
                    .mod(pMinusOne);

        } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));

        return new Signature(r, s);
    }

    // Проверка подписи
    public boolean verifyFile(File file, Signature signature, KeyPair keyPair) throws Exception {
        return verifyFile(file, signature, keyPair.publicKey, keyPair.params);
    }

    public boolean verifyFile(File file, Signature signature, BigInteger publicKey, SystemParams params) throws Exception {
        byte[] fileData = java.nio.file.Files.readAllBytes(file.toPath());
        BigInteger hash = new BigInteger(1, hash(fileData));

        // Проверка диапазонов
        if (signature.r.compareTo(BigInteger.ONE) < 0 ||
                signature.r.compareTo(params.p) >= 0 ||
                signature.s.compareTo(BigInteger.ONE) < 0 ||
                signature.s.compareTo(params.p.subtract(BigInteger.ONE)) >= 0) {
            return false;
        }

        // Проверка: g^hash ≡ y^r * r^s (mod p)
        BigInteger left = params.g.modPow(hash, params.p);
        BigInteger right = publicKey.modPow(signature.r, params.p)
                .multiply(signature.r.modPow(signature.s, params.p))
                .mod(params.p);

        return left.equals(right);
    }

    private byte[] hash(byte[] message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(message);
    }
}