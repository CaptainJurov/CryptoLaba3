package Laba;

import java.io.*;

public class KeyManager {
    private static final String KEYS_DIRECTORY = "keys/";

    static {
        new File(KEYS_DIRECTORY).mkdirs();
    }

    // Сохранение параметров системы
    public static void saveSystemParams(String algorithm, Object params) throws IOException {
        String filename = KEYS_DIRECTORY + algorithm + "_system.params";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(params);
        }
    }

    public static Object loadSystemParams(String algorithm) throws IOException, ClassNotFoundException {
        String filename = KEYS_DIRECTORY + algorithm + "_system.params";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }
    }

    public static void saveKeyPair(String algorithm, String keyName, Object keyPair) throws IOException {
        String filename = KEYS_DIRECTORY + algorithm + "_" + keyName + ".key";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(keyPair);
        }
    }

    public static Object loadKeyPair(String algorithm, String keyName) throws IOException, ClassNotFoundException {
        String filename = KEYS_DIRECTORY + algorithm + "_" + keyName + ".key";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }
    }

    public static void saveSignature(String algorithm, String sigName, Object signature) throws IOException {
        String filename = KEYS_DIRECTORY + algorithm + "_" + sigName + ".sig";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(signature);
        }
    }

    public static Object loadSignature(String algorithm, String sigName) throws IOException, ClassNotFoundException {
        String filename = KEYS_DIRECTORY + algorithm + "_" + sigName + ".sig";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }
    }

    public static boolean systemParamsExist(String algorithm) {
        String filename = KEYS_DIRECTORY + algorithm + "_system.params";
        return new File(filename).exists();
    }

    public static boolean keyExists(String algorithm, String keyName) {
        String filename = KEYS_DIRECTORY + algorithm + "_" + keyName + ".key";
        return new File(filename).exists();
    }

    public static boolean signatureExists(String algorithm, String sigName) {
        String filename = KEYS_DIRECTORY + algorithm + "_" + sigName + ".sig";
        return new File(filename).exists();
    }
}