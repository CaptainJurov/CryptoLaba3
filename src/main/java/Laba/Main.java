package Laba;

import Laba.Crypto.ElGamal;
import Laba.Crypto.RSA;
import Laba.Crypto.GOST;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {
    public static final String WHITE = "\u001B[38;5;255m";
    public static final String BLUE = "\u001B[38;5;21m";
    public static final String RED = "\u001B[38;5;196m";
    public static final String GREEN = "\u001B[38;5;46m";
    public static final String YELLOW = "\u001B[38;5;226m";
    public static final String RESET = "\u001B[0m";

    private static Scanner scanner = new Scanner(System.in);
    private static final String KEY_NAME = "default";

    public static void main(String[] args) {
        printHeader();

        try {
            while (true) {
                System.out.println(WHITE + "═".repeat(50) + RESET);
                System.out.println(GREEN + "🎯 ГЛАВНОЕ МЕНЮ:" + RESET);
                System.out.println(WHITE + "1. ✍️  СОЗДАНИЕ КЛЮЧЕЙ И ПОДПИСИ");
                System.out.println("2. 🔍 ПРОВЕРКА ПОДПИСИ");
                System.out.println("3. 🔑 ТОЛЬКО ГЕНЕРАЦИЯ КЛЮЧЕЙ");
                System.out.println("4. 🗑️  УДАЛИТЬ ВСЕ КЛЮЧИ И ПОДПИСИ");
                System.out.println("0. 🚪 ВЫХОД");
                System.out.print(YELLOW + "\nВведите номер команды: " + RESET);

                int command = scanner.nextInt();
                scanner.nextLine();

                switch (command) {
                    case 1:
                        generateKeysAndSign();
                        break;
                    case 2:
                        verifySignatures();
                        break;
                    case 3:
                        generateKeysOnly();
                        break;
                    case 4:
                        deleteAllKeys();
                        break;
                    case 0:
                        System.out.println(GREEN + "\n👋 До свидания!" + RESET);
                        return;
                    default:
                        System.out.println(RED + "❌ Неверная команда!" + RESET);
                }

                System.out.println("\n" + WHITE + "─".repeat(50) + RESET);
                System.out.print(YELLOW + "Нажмите Enter для продолжения..." + RESET);
                scanner.nextLine();
            }

        } catch (Exception e) {
            System.out.println(RED + "❌ Ошибка: " + e.getMessage() + RESET);
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void generateKeysAndSign() throws Exception {
        System.out.println(BLUE + "\n" + "🔷".repeat(25));
        System.out.println("   СОЗДАНИЕ КЛЮЧЕЙ И ПОДПИСИ");
        System.out.println("🔷".repeat(25) + RESET);

        System.out.print(WHITE + "Введите путь к файлу для подписи: " + RESET);
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(RED + "❌ Файл не найден!" + RESET);
            return;
        }

        System.out.println(WHITE + "📄 Файл: " + file.getName());
        System.out.println("📏 Размер: " + file.length() + " байт" + RESET);

        System.out.println(YELLOW + "\n🔑 Генерация ключей и параметров..." + RESET);

        // ElGamal
        System.out.println(WHITE + "\n🔷 ELGAMAL:" + RESET);
        ElGamal.SystemParams elGamalParams;
        if (KeyManager.systemParamsExist("elgamal")) {
            elGamalParams = (ElGamal.SystemParams) KeyManager.loadSystemParams("elgamal");
            System.out.println("   📁 Загружены существующие параметры");
        } else {
            elGamalParams = ElGamal.generateSystemParams();
            KeyManager.saveSystemParams("elgamal", elGamalParams);
            System.out.println("   ✅ Созданы новые параметры");
        }

        ElGamal elGamal = new ElGamal();
        ElGamal.KeyPair elGamalKeys = elGamal.generateKeyPair(elGamalParams);
        KeyManager.saveKeyPair("elgamal", KEY_NAME, elGamalKeys);
        ElGamal.Signature elGamalSig = elGamal.signFile(file, elGamalKeys);
        KeyManager.saveSignature("elgamal", file.getName(), elGamalSig);
        System.out.println("   ✅ Ключи сгенерированы и подпись создана");

        // RSA
        System.out.println(WHITE + "\n⚪ RSA:" + RESET);
        RSA rsa = new RSA();
        RSA.KeyPair rsaKeys = rsa.generateKeyPair();
        KeyManager.saveKeyPair("rsa", KEY_NAME, rsaKeys);
        RSA.Signature rsaSig = rsa.signFile(file, rsaKeys);
        KeyManager.saveSignature("rsa", file.getName(), rsaSig);
        System.out.println("   ✅ Ключи сгенерированы и подпись создана");

        // GOST
        System.out.println(WHITE + "\n🔴 GOST:" + RESET);
        GOST.SystemParams gostParams;
        if (KeyManager.systemParamsExist("gost")) {
            gostParams = (GOST.SystemParams) KeyManager.loadSystemParams("gost");
            System.out.println("   📁 Загружены существующие параметры");
        } else {
            gostParams = GOST.generateSystemParams();
            KeyManager.saveSystemParams("gost", gostParams);
            System.out.println("   ✅ Созданы новые параметры");
        }

        GOST gost = new GOST();
        GOST.KeyPair gostKeys = gost.generateKeyPair(gostParams);
        KeyManager.saveKeyPair("gost", KEY_NAME, gostKeys);
        GOST.Signature gostSig = gost.signFile(file, gostKeys);
        KeyManager.saveSignature("gost", file.getName(), gostSig);
        System.out.println("   ✅ Ключи сгенерированы и подпись создана");

        System.out.println(GREEN + "\n🎉 ВСЕ ПОДПИСИ УСПЕШНО СОЗДАНЫ!" + RESET);
        printFileInfo();
    }

    private static void verifySignatures() throws Exception {
        System.out.println(RED + "\n" + "🔍".repeat(25));
        System.out.println("       ПРОВЕРКА ПОДПИСЕЙ");
        System.out.println("🔍".repeat(25) + RESET);

        System.out.print(WHITE + "Введите путь к файлу для проверки: " + RESET);
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(RED + "❌ Файл не найден!" + RESET);
            return;
        }

        System.out.println(WHITE + "📄 Файл: " + file.getName());
        System.out.println("📏 Размер: " + file.length() + " байт" + RESET);

        System.out.println(YELLOW + "\n🔎 Проверка подписей..." + RESET);

        boolean[] results = new boolean[3];
        String[] algorithms = {"ELGAMAL", "RSA", "GOST"};
        String[] statusMessages = new String[3];
        String[] details = new String[3];

        // Проверка ELGAMAL
        try {
            if (KeyManager.signatureExists("elgamal", file.getName()) &&
                    KeyManager.keyExists("elgamal", KEY_NAME)) {

                ElGamal.Signature elGamalSig = (ElGamal.Signature)
                        KeyManager.loadSignature("elgamal", file.getName());
                ElGamal.KeyPair elGamalKeys = (ElGamal.KeyPair)
                        KeyManager.loadKeyPair("elgamal", KEY_NAME);

                ElGamal elGamal = new ElGamal();
                results[0] = elGamal.verifyFile(file, elGamalSig, elGamalKeys);
                statusMessages[0] = results[0] ? "✅ ПОДЛИННА" : "❌ НЕДЕЙСТВИТЕЛЬНА";
                details[0] = results[0] ? "Надежно как ВДВ!" : "Файл изменен!";
            } else {
                statusMessages[0] = "⚠️  ОТСУТСТВУЕТ";
                details[0] = "Ключи или подпись не найдены";
                results[0] = false;
            }
        } catch (Exception e) {
            statusMessages[0] = "💔 ОШИБКА";
            details[0] = "Ошибка проверки: " + e.getMessage();
            results[0] = false;
        }

        // Проверка RSA
        try {
            if (KeyManager.signatureExists("rsa", file.getName()) &&
                    KeyManager.keyExists("rsa", KEY_NAME)) {

                RSA.Signature rsaSig = (RSA.Signature)
                        KeyManager.loadSignature("rsa", file.getName());
                RSA.KeyPair rsaKeys = (RSA.KeyPair)
                        KeyManager.loadKeyPair("rsa", KEY_NAME);

                RSA rsa = new RSA();
                results[1] = rsa.verifyFile(file, rsaSig, rsaKeys.publicKey, rsaKeys.e);
                statusMessages[1] = results[1] ? "✅ ПОДЛИННА" : "❌ НЕДЕЙСТВИТЕЛЬНА";
                details[1] = results[1] ? "Крепка как сталь!" : "Файл изменен!";
            } else {
                statusMessages[1] = "⚠️  ОТСУТСТВУЕТ";
                details[1] = "Ключи или подпись не найдены";
                results[1] = false;
            }
        } catch (Exception e) {
            statusMessages[1] = "💔 ОШИБКА";
            details[1] = "Ошибка проверки: " + e.getMessage();
            results[1] = false;
        }

        // Проверка GOST
        try {
            if (KeyManager.signatureExists("gost", file.getName()) &&
                    KeyManager.keyExists("gost", KEY_NAME)) {

                GOST.Signature gostSig = (GOST.Signature)
                        KeyManager.loadSignature("gost", file.getName());
                GOST.KeyPair gostKeys = (GOST.KeyPair)
                        KeyManager.loadKeyPair("gost", KEY_NAME);

                GOST gost = new GOST();
                results[2] = gost.verifyFile(file, gostSig, gostKeys);
                statusMessages[2] = results[2] ? "✅ ПОДЛИННА" : "❌ НЕДЕЙСТВИТЕЛЬНА";
                details[2] = results[2] ? "Соответствует ГОСТу!" : "Файл изменен!";
            } else {
                statusMessages[2] = "⚠️  ОТСУТСТВУЕТ";
                details[2] = "Ключи или подпись не найдены";
                results[2] = false;
            }
        } catch (Exception e) {
            statusMessages[2] = "💔 ОШИБКА";
            details[2] = "Ошибка проверки: " + e.getMessage();
            results[2] = false;
        }

        printVerificationResults(results, algorithms, statusMessages, details);
    }

    private static void generateKeysOnly() throws Exception {
        System.out.println(GREEN + "\n" + "🔑".repeat(25));
        System.out.println("   ГЕНЕРАЦИЯ КЛЮЧЕЙ");
        System.out.println("🔑".repeat(25) + RESET);

        System.out.println(YELLOW + "\n🔑 Генерация ключей..." + RESET);

        // ElGamal
        System.out.println(WHITE + "\n🔷 ELGAMAL:" + RESET);
        ElGamal.SystemParams elGamalParams = ElGamal.generateSystemParams();
        KeyManager.saveSystemParams("elgamal", elGamalParams);
        ElGamal elGamal = new ElGamal();
        ElGamal.KeyPair elGamalKeys = elGamal.generateKeyPair(elGamalParams);
        KeyManager.saveKeyPair("elgamal", KEY_NAME, elGamalKeys);
        System.out.println("   ✅ Параметры и ключи сгенерированы");

        // RSA
        System.out.println(WHITE + "\n⚪ RSA:" + RESET);
        RSA rsa = new RSA();
        RSA.KeyPair rsaKeys = rsa.generateKeyPair();
        KeyManager.saveKeyPair("rsa", KEY_NAME, rsaKeys);
        System.out.println("   ✅ Ключи сгенерированы");

        // GOST
        System.out.println(WHITE + "\n🔴 GOST:" + RESET);
        GOST.SystemParams gostParams = GOST.generateSystemParams();
        KeyManager.saveSystemParams("gost", gostParams);
        GOST gost = new GOST();
        GOST.KeyPair gostKeys = gost.generateKeyPair(gostParams);
        KeyManager.saveKeyPair("gost", KEY_NAME, gostKeys);
        System.out.println("   ✅ Параметры и ключи сгенерированы");

        System.out.println(GREEN + "\n🎉 ВСЕ КЛЮЧИ УСПЕШНО СОЗДАНЫ!" + RESET);
        printFileInfo();
    }

    private static void deleteAllKeys() throws Exception {
        System.out.println(RED + "\n" + "🗑️".repeat(25));
        System.out.println("   УДАЛЕНИЕ КЛЮЧЕЙ И ПОДПИСЕЙ");
        System.out.println("🗑️".repeat(25) + RESET);

        System.out.print(YELLOW + "Вы уверены? (y/N): " + RESET);
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("y")) {
            System.out.println(WHITE + "❌ Отменено" + RESET);
            return;
        }

        File keysDir = new File("keys/");
        if (!keysDir.exists()) {
            System.out.println(WHITE + "ℹ️ Папка keys/ не существует" + RESET);
            return;
        }

        int deletedCount = 0;
        File[] files = keysDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                    deletedCount++;
                }
            }
        }

        if (keysDir.delete()) {
            System.out.println(GREEN + "✅ Папка keys/ удалена" + RESET);
        }

        System.out.println(WHITE + "🗑️ Удалено файлов: " + deletedCount + RESET);
        System.out.println(GREEN + "🎉 Все ключи и подписи удалены!" + RESET);
    }

    private static void printVerificationResults(boolean[] results, String[] algorithms,
                                                 String[] statusMessages, String[] details) {
        System.out.println(WHITE + "\n" + "═".repeat(60));
        System.out.println("📊 РЕЗУЛЬТАТЫ ПРОВЕРКИ ПОДПИСЕЙ");
        System.out.println("═".repeat(60) + RESET);

        System.out.println(BLUE + "┌───────────────────┬──────────────────┬────────────────────────────┐");
        System.out.println("│     Алгоритм      │     Статус      │         Детали               │");
        System.out.println("├───────────────────┼──────────────────┼────────────────────────────┤");

        for (int i = 0; i < results.length; i++) {
            String statusColor = "";
            if (statusMessages[i].startsWith("✅")) statusColor = GREEN;
            else if (statusMessages[i].startsWith("❌")) statusColor = RED;
            else if (statusMessages[i].startsWith("⚠️")) statusColor = YELLOW;
            else statusColor = RED;

            System.out.printf("│      %-11s │   %s%-12s%s │   %-25s │\n",
                    algorithms[i],
                    statusColor, statusMessages[i], BLUE,
                    details[i]);
        }

        System.out.println("└───────────────────┴──────────────────┴────────────────────────────┘" + RESET);

        // Статистика
        int validCount = 0;
        for (boolean result : results) {
            if (result) validCount++;
        }

        System.out.println(WHITE + "\n📈 СТАТИСТИКА: " + validCount + "/3 подписей подлинны" + RESET);

        if (validCount == 3) {
            System.out.println(GREEN + "\n🎉 ВСЕ ПОДПИСИ ВЕРНЫ!" + RESET);
            System.out.println(WHITE + "💪 Файл аутентичен и не изменялся" + RESET);
            System.out.println(BLUE + "🇷🇺 СЛАВА РОССИЙСКОЙ КРИПТОГРАФИИ! 🇷🇺" + RESET);
        } else if (validCount > 0) {
            System.out.println(YELLOW + "\n⚠️  ЧАСТИЧНАЯ АУТЕНТИЧНОСТЬ!" + RESET);
            System.out.println(WHITE + "🔧 Некоторые подписи отсутствуют или недействительны" + RESET);
        } else {
            System.out.println(RED + "\n🚨 ФАЙЛ НЕ АУТЕНТИЧЕН!" + RESET);
            System.out.println(WHITE + "💀 Файл был изменен или подписи подделаны!" + RESET);
        }
    }

    private static void printFileInfo() {
        File keysDir = new File("keys/");
        if (keysDir.exists()) {
            File[] files = keysDir.listFiles();
            if (files != null && files.length > 0) {
                System.out.println(WHITE + "\n📁 Содержимое папки keys/:" + RESET);
                for (File file : files) {
                    System.out.println("   📄 " + file.getName() + " (" + file.length() + " байт)");
                }
            }
        }
    }

    private static void printHeader() {
        System.out.println(WHITE + "████████████████████████████████████████");
        System.out.println(BLUE + "████████████████████████████████████████");
        System.out.println(RED + "████████████████████████████████████████" + RESET);
        System.out.println(WHITE + "\n🇷🇺  РОССИЙСКАЯ БИБЛИОТЕКА ЭЛЕКТРОННОЙ ПОДПИСИ  🇷🇺" + RESET);
        System.out.println(BLUE + "       Защита данных для величия Родины!" + RESET);
        System.out.println(WHITE + "           Алгоритмы: ElGamal, RSA, GOST" + RESET);
        System.out.println();
    }
}