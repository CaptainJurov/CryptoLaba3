package Laba.Crypto;
import Laba.Main;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Random;

public class ElGamal implements Serializable {
    private transient BigInteger p, g, x, y;
    private transient Random random = new Random();

    public static class SignatureResult implements Serializable {
        public BigInteger r;
        public BigInteger s;
        public BigInteger p;
        public BigInteger g;
        public BigInteger y;

        public SignatureResult(BigInteger r, BigInteger s, BigInteger p, BigInteger g, BigInteger y) {
            this.r = r;
            this.s = s;
            this.p = p;
            this.g = g;
            this.y = y;
        }
    }

    public ElGamal() {
        generateKeys();
    }

    private void generateKeys() {
        p = BigInteger.probablePrime(512, random);
        g = new BigInteger("2");
        x = new BigInteger(256, random);
        y = g.modPow(x, p);

        System.out.println(Main.BLUE + "🇷🇺 Ключи Эль-Гамаля сгенерированы с русской надежностью!" + Main.RESET);
    }

    public SignatureResult signFile(File file) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            BigInteger m = new BigInteger(1, hash(fileData));
            BigInteger k;
            BigInteger r;
            BigInteger s;

            do {
                k = new BigInteger(p.bitLength() - 1, random);
                r = g.modPow(k, p);
                s = k.modInverse(p.subtract(BigInteger.ONE))
                        .multiply(m.subtract(x.multiply(r)))
                        .mod(p.subtract(BigInteger.ONE));
            } while (s.equals(BigInteger.ZERO));

            System.out.println(Main.WHITE + "✅ Подпись Эль-Гамаля создана с русской точностью!" + Main.RESET);
            return new SignatureResult(r, s, p, g, y);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подписи Эль-Гамаля!", e);
        }
    }

    public boolean verifyFile(File file, SignatureResult signature) {
        try {
            if (signature.r.compareTo(BigInteger.ONE) < 0 ||
                    signature.r.compareTo(signature.p) >= 0 ||
                    signature.s.compareTo(BigInteger.ONE) < 0 ||
                    signature.s.compareTo(signature.p.subtract(BigInteger.ONE)) >= 0) {
                return false;
            }

            byte[] fileData = Files.readAllBytes(file.toPath());
            BigInteger m = new BigInteger(1, hash(fileData));
            BigInteger v1 = signature.y.modPow(signature.r, signature.p)
                    .multiply(signature.r.modPow(signature.s, signature.p))
                    .mod(signature.p);
            BigInteger v2 = signature.g.modPow(m, signature.p);

            boolean valid = v1.equals(v2);
            System.out.println(valid ?
                    Main.WHITE + "🇷🇺 Подпись Эль-Гамаля верифицирована - доверяем как нашему Президенту!" + Main.RESET :
                    Main.RED + "⚠️ Подпись Эль-Гамаля не прошла проверку - бдительность превыше всего!" + Main.RESET);
            return valid;
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] hash(byte[] message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(message);
    }
}