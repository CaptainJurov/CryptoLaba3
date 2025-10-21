package Laba;
import Laba.Crypto.ElGamal;
import Laba.Crypto.GOST;
import Laba.Crypto.RSA;


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

    public static void main(String[] args) {
        printHeader();

        try {
            System.out.println(WHITE + "═".repeat(50) + RESET);
            System.out.println(GREEN + "🎯 ВЫБЕРИТЕ РЕЖИМ РАБОТЫ:" + RESET);
            System.out.println(WHITE + "1. ✍️  СОЗДАНИЕ ПОДПИСИ");
            System.out.println("2. 🔍 ПРОВЕРКА ПОДПИСИ");
            System.out.print(YELLOW + "\nВведите номер режима: " + RESET);

            int mode = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (mode) {
                case 1:
                    signatureMode();
                    break;
                case 2:
                    verificationMode();
                    break;
                default:
                    System.out.println(RED + "❌ Неверный выбор режима!" + RESET);
            }

        } catch (Exception e) {
            System.out.println(RED + "❌ Ошибка: " + e.getMessage() + RESET);
        } finally {
            scanner.close();
        }
    }

    private static void signatureMode() throws Exception {
        System.out.println(BLUE + "\n" + "🔷".repeat(25));
        System.out.println("       РЕЖИМ СОЗДАНИЯ ПОДПИСИ");
        System.out.println("🔷".repeat(25) + RESET);

        System.out.print(WHITE + "Введите путь к файлу для подписи: " + RESET);
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(RED + "❌ Файл не найден!" + RESET);
            return;
        }

        System.out.println(WHITE + "📄 Файл для подписи: " + file.getName());
        System.out.println("📝 Содержимое: " + RED +
                new String(Files.readAllBytes(file.toPath())).substring(0,
                        Math.min(50, Files.readAllBytes(file.toPath()).length)) + "..." + WHITE);

        System.out.println("\n" + "─".repeat(50) + RESET);

        // Создание подписей
        System.out.println(BLUE + "\n🔷 АЛГОРИТМ ELGAMAL" + RESET);
        ElGamal elGamal = new ElGamal();
        ElGamal.SignatureResult elGamalResult = elGamal.signFile(file);
        saveSignature("elgamal_signature.sig", elGamalResult);

        System.out.println(WHITE + "\n⚪ АЛГОРИТМ RSA" + RESET);
        RSA rsa = new RSA();
        RSA.SignatureResult rsaResult = rsa.signFile(file);
        saveSignature("rsa_signature.sig", rsaResult);

        System.out.println(RED + "\n🔴 АЛГОРИТМ GOST" + RESET);
        GOST gost = new GOST();
        GOST.SignatureResult gostResult = gost.signFile(file);
        saveSignature("gost_signature.sig", gostResult);

        System.out.println(GREEN + "\n✅ ВСЕ ПОДПИСИ СОЗДАНЫ И СОХРАНЕНЫ!" + RESET);
        System.out.println(WHITE + "📁 Файлы подписей:");
        System.out.println("   - elgamal_signature.sig");
        System.out.println("   - rsa_signature.sig");
        System.out.println("   - gost_signature.sig");
        System.out.println(BLUE + "\n🇷🇺 Данные под защитой Российской Федерации! 🇷🇺" + RESET);
    }

    private static void verificationMode() throws Exception {
        System.out.println(RED + "\n" + "🔍".repeat(25));
        System.out.println("       РЕЖИМ ПРОВЕРКИ ПОДПИСИ");
        System.out.println("🔍".repeat(25) + RESET);

        System.out.print(WHITE + "Введите путь к файлу для проверки: " + RESET);
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(RED + "❌ Файл не найден!" + RESET);
            return;
        }

        System.out.println(WHITE + "📄 Проверяемый файл: " + file.getName());
        System.out.println("📝 Содержимое: " + RED +
                new String(Files.readAllBytes(file.toPath())).substring(0,
                        Math.min(50, Files.readAllBytes(file.toPath()).length)) + "..." + WHITE);

        System.out.println(YELLOW + "\n🔎 Проверяем все доступные подписи..." + RESET);

        boolean[] results = new boolean[3];
        String[] algorithms = {"ELGAMAL", "RSA", "GOST"};
        String[] statusMessages = new String[3];
        String[] details = new String[3];

        // Проверка ELGAMAL
        try {
            File sig1 = new File("elgamal_signature.sig");
            if (sig1.exists()) {
                ElGamal.SignatureResult elGamalSig = loadSignature("elgamal_signature.sig", ElGamal.SignatureResult.class);
                ElGamal elGamal = new ElGamal();
                results[0] = elGamal.verifyFile(file, elGamalSig);
                statusMessages[0] = results[0] ? "✅ ПОДЛИННА" : "❌ НЕДЕЙСТВИТЕЛЬНА";
                details[0] = results[0] ? "Надежно как ВДВ!" : "Файл изменен или подпись повреждена!";
            } else {
                statusMessages[0] = "⚠️  ОТСУТСТВУЕТ";
                details[0] = "Файл подписи не найден";
                results[0] = false;
            }
        } catch (Exception e) {
            statusMessages[0] = "💔 ОШИБКА";
            details[0] = "Ошибка проверки: " + e.getMessage();
            results[0] = false;
        }

        // Проверка RSA
        try {
            File sig2 = new File("rsa_signature.sig");
            if (sig2.exists()) {
                RSA.SignatureResult rsaSig = loadSignature("rsa_signature.sig", RSA.SignatureResult.class);
                RSA rsa = new RSA();
                results[1] = rsa.verifyFile(file, rsaSig);
                statusMessages[1] = results[1] ? "✅ ПОДЛИННА" : "❌ НЕДЕЙСТВИТЕЛЬНА";
                details[1] = results[1] ? "Крепка как сталь!" : "Файл изменен или подпись повреждена!";
            } else {
                statusMessages[1] = "⚠️  ОТСУТСТВУЕТ";
                details[1] = "Файл подписи не найден";
                results[1] = false;
            }
        } catch (Exception e) {
            statusMessages[1] = "💔 ОШИБКА";
            details[1] = "Ошибка проверки: " + e.getMessage();
            results[1] = false;
        }

        // Проверка GOST
        try {
            File sig3 = new File("gost_signature.sig");
            if (sig3.exists()) {
                GOST.SignatureResult gostSig = loadSignature("gost_signature.sig", GOST.SignatureResult.class);
                GOST gost = new GOST();
                results[2] = gost.verifyFile(file, gostSig);
                statusMessages[2] = results[2] ? "✅ ПОДЛИННА" : "❌ НЕДЕЙСТВИТЕЛЬНА";
                details[2] = results[2] ? "Соответствует ГОСТу!" : "Файл изменен или подпись повреждена!";
            } else {
                statusMessages[2] = "⚠️  ОТСУТСТВУЕТ";
                details[2] = "Файл подписи не найден";
                results[2] = false;
            }
        } catch (Exception e) {
            statusMessages[2] = "💔 ОШИБКА";
            details[2] = "Ошибка проверки: " + e.getMessage();
            results[2] = false;
        }

        printVerificationResults(results, algorithms, statusMessages, details);
    }

    private static void printVerificationResults(boolean[] results, String[] algorithms, String[] statusMessages, String[] details) {
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
        int missingCount = 0;
        int invalidCount = 0;
        int errorCount = 0;

        for (String status : statusMessages) {
            if (status.startsWith("✅")) validCount++;
            else if (status.startsWith("⚠️")) missingCount++;
            else if (status.startsWith("❌")) invalidCount++;
            else if (status.startsWith("💔")) errorCount++;
        }

        System.out.println(WHITE + "\n📈 СТАТИСТИКА:" + RESET);
        System.out.println(GREEN + "   ✅ Подлинных подписей: " + validCount + "/3");
        System.out.println(YELLOW + "   ⚠️  Отсутствующих подписей: " + missingCount + "/3");
        System.out.println(RED + "   ❌ Недействительных подписей: " + invalidCount + "/3");
        if (errorCount > 0) {
            System.out.println(RED + "   💔 Ошибок проверки: " + errorCount + "/3");
        }

        if (validCount == 3) {
            System.out.println(GREEN + "\n🎉 ВСЕ ПОДПИСИ ВЕРНЫ!" + RESET);
            System.out.println(WHITE + "💪 Данные под защитой Российской Федерации!" + RESET);
            System.out.println(BLUE + "🇷🇺 СЛАВА РОССИИ! 🇷🇺" + RESET);
        } else if (validCount > 0) {
            System.out.println(YELLOW + "\n⚠️  ЧАСТИЧНАЯ ЗАЩИТА!" + RESET);
            System.out.println(WHITE + "🔧 Рекомендуется повторное создание отсутствующих подписей" + RESET);
        } else {
            System.out.println(RED + "\n🚨 ВНИМАНИЕ! НЕТ ДЕЙСТВИТЕЛЬНЫХ ПОДПИСЕЙ!" + RESET);
            System.out.println(WHITE + "💀 Файл не защищен или был изменен!" + RESET);
        }

        System.out.println(WHITE + "\nРабота завершена с честью и достоинством!" + RESET);
    }

    private static void printHeader() {
        System.out.println(WHITE + "████████████████████████████████████████");
        System.out.println(BLUE + "████████████████████████████████████████");
        System.out.println(RED + "████████████████████████████████████████" + RESET);
        System.out.println(WHITE + "\n🇷🇺  РОССИЙСКАЯ БИБЛИОТЕКА ЭЛЕКТРОННОЙ ПОДПИСИ  🇷🇺" + RESET);
        System.out.println(BLUE + "       Защита данных для величия Родины!" + RESET);
        System.out.println();
    }

    private static void saveSignature(String filename, Object signature) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(signature);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T loadSignature(String filename, Class<T> clazz) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (T) ois.readObject();
        }
    }
}