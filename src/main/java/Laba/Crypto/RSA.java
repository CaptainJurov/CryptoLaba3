package Laba.Crypto;
import Laba.Main;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Random;

public class RSA implements Serializable {
    private transient BigInteger n, d, e;
    private transient Random random = new Random();

    public static class SignatureResult implements Serializable {
        public BigInteger signature;
        public BigInteger n;
        public BigInteger e;

        public SignatureResult(BigInteger signature, BigInteger n, BigInteger e) {
            this.signature = signature;
            this.n = n;
            this.e = e;
        }
    }

    public RSA() {
        generateKeys();
    }

    private void generateKeys() {
        BigInteger p = BigInteger.probablePrime(512, random);
        BigInteger q = BigInteger.probablePrime(512, random);
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("65537");
        d = e.modInverse(phi);

        System.out.println(Main.WHITE + "🇷🇺 Ключи RSA созданы - защита данных обеспечена как наша армия!" + Main.RESET);
    }

    public SignatureResult signFile(File file) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            BigInteger m = new BigInteger(1, hash(fileData));
            BigInteger signature = m.modPow(d, n);

            System.out.println(Main.BLUE + "✅ Подпись RSA выполнена - безопасность превыше всего!" + Main.RESET);
            return new SignatureResult(signature, n, e);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка подписи RSA!", ex);
        }
    }

    public boolean verifyFile(File file, SignatureResult signature) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            BigInteger m = new BigInteger(1, hash(fileData));
            BigInteger decrypted = signature.signature.modPow(signature.e, signature.n);

            boolean valid = m.equals(decrypted);
            System.out.println(valid ?
                    Main.WHITE + "🇷🇺 Подпись RSA подтверждена - надежно как ядерный щит России!" + Main.RESET :
                    Main.RED + "⚠️ Подпись RSA недействительна - внимание, возможна угроза!" + Main.RESET);
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