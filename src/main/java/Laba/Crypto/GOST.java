package Laba.Crypto;
import Laba.Main;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Random;

public class GOST implements Serializable {
    private transient BigInteger p, q, a, x, y;
    private transient Random random = new Random();

    public static class SignatureResult implements Serializable {
        public BigInteger r;
        public BigInteger s;
        public BigInteger p;
        public BigInteger q;
        public BigInteger a;
        public BigInteger y;

        public SignatureResult(BigInteger r, BigInteger s, BigInteger p,
                               BigInteger q, BigInteger a, BigInteger y) {
            this.r = r;
            this.s = s;
            this.p = p;
            this.q = q;
            this.a = a;
            this.y = y;
        }
    }

    public GOST() {
        generateKeys();
    }

    private void generateKeys() {
        q = BigInteger.probablePrime(256, random);
        p = q.multiply(new BigInteger("2")).add(BigInteger.ONE);
        while (!p.isProbablePrime(100)) {
            q = BigInteger.probablePrime(256, random);
            p = q.multiply(new BigInteger("2")).add(BigInteger.ONE);
        }

        a = new BigInteger("2").modPow(p.subtract(BigInteger.ONE).divide(q), p);
        x = new BigInteger(255, random);
        y = a.modPow(x, p);

        System.out.println(Main.RED + "🇷🇺 Параметры ГОСТ установлены - российские стандарты самые строгие!" + Main.RESET);
    }

    public SignatureResult signFile(File file) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            BigInteger h = new BigInteger(1, hash(fileData));
            BigInteger k;
            BigInteger r;
            BigInteger s;

            do {
                k = new BigInteger(q.bitLength() - 1, random);
                r = a.modPow(k, p).mod(q);
                s = k.multiply(h).add(x.multiply(r)).mod(q);
            } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));

            System.out.println(Main.WHITE + "✅ Подпись ГОСТ создана - соответствует всем стандартам РФ!" + Main.RESET);
            return new SignatureResult(r, s, p, q, a, y);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подписи ГОСТ!", e);
        }
    }

    public boolean verifyFile(File file, SignatureResult signature) {
        try {
            if (signature.r.compareTo(BigInteger.ZERO) <= 0 ||
                    signature.r.compareTo(signature.q) >= 0 ||
                    signature.s.compareTo(BigInteger.ZERO) <= 0 ||
                    signature.s.compareTo(signature.q) >= 0) {
                return false;
            }

            byte[] fileData = Files.readAllBytes(file.toPath());
            BigInteger h = new BigInteger(1, hash(fileData));
            BigInteger v = h.modPow(signature.q.subtract(new BigInteger("2")), signature.q);
            BigInteger z1 = signature.s.multiply(v).mod(signature.q);
            BigInteger z2 = signature.q.subtract(signature.r).multiply(v).mod(signature.q);
            BigInteger u = signature.a.modPow(z1, signature.p)
                    .multiply(signature.y.modPow(z2, signature.p))
                    .mod(signature.p)
                    .mod(signature.q);

            boolean valid = u.equals(signature.r);
            System.out.println(valid ?
                    Main.BLUE + "🇷🇺 Подпись ГОСТ верифицирована - российское качество подтверждено!" + Main.RESET :
                    Main.RED + "⚠️ Подпись ГОСТ не прошла проверку - только лучшее для России!" + Main.RESET);
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