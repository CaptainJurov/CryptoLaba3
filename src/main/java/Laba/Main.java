package Laba;
import Laba.Crypto.ElGamal;
import Laba.Crypto.GOST;
import Laba.Crypto.RSA;

import java.io.*;
import java.nio.file.Files;

public class Main {
    public static final String WHITE = "\u001B[38;5;255m";
    public static final String BLUE = "\u001B[38;5;21m";
    public static final String RED = "\u001B[38;5;196m";
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        printHeader();

        try {
            // Создаем тестовый файл
            String testContent = "За Россию! За Президента! За победу!";
            File testFile = new File("russian_file.txt");
            Files.write(testFile.toPath(), testContent.getBytes());

            System.out.println(WHITE + "📄 Файл для подписи: " + testFile.getName());
            System.out.println("📝 Содержимое: " + RED + testContent + WHITE);

            System.out.println("\n" + "─".repeat(50) + RESET);

            // Эль-Гамаль
            System.out.println(BLUE + "\n🔷 АЛГОРИТМ ELGAMAL" + RESET);
            ElGamal elGamal = new ElGamal();
            ElGamal.SignatureResult elGamalResult = elGamal.signFile(testFile);
            boolean elGamalValid = elGamal.verifyFile(testFile, elGamalResult);
            printStatus("ELGAMAL", elGamalValid);

            // RSA
            System.out.println(WHITE + "\n⚪ АЛГОРИТМ RSA" + RESET);
            RSA rsa = new RSA();
            RSA.SignatureResult rsaResult = rsa.signFile(testFile);
            boolean rsaValid = rsa.verifyFile(testFile, rsaResult);
            printStatus("RSA", rsaValid);

            // ГОСТ
            System.out.println(RED + "\n🔴 АЛГОРИТМ GOST" + RESET);
            GOST gost = new GOST();
            GOST.SignatureResult gostResult = gost.signFile(testFile);
            boolean gostValid = gost.verifyFile(testFile, gostResult);
            printStatus("GOST", gostValid);

            System.out.println("\n" + "═".repeat(50));
            printFinalResults(elGamalValid, rsaValid, gostValid);

        } catch (Exception e) {
            System.out.println(RED + "❌ Ошибка: " + e.getMessage() + RESET);
        }
    }

    private static void printHeader() {
        System.out.println(WHITE + "████████████████████████████████████████");
        System.out.println(BLUE + "████████████████████████████████████████");
        System.out.println(RED + "████████████████████████████████████████" + RESET);
        System.out.println(WHITE + "\n🇷🇺  РОССИЙСКАЯ БИБЛИОТЕКА ЭЛЕКТРОННОЙ ПОДПИСИ  🇷🇺" + RESET);
        System.out.println(BLUE + "       Защита данных для величия Родины!" + RESET);
        System.out.println();
    }

    private static void printStatus(String algorithm, boolean isValid) {
        if (isValid) {
            switch (algorithm) {
                case "ELGAMAL":
                    System.out.println(WHITE + "✅ Подпись верифицирована - надежно как ВДВ!" + RESET);
                    break;
                case "RSA":
                    System.out.println(WHITE + "✅ Подпись подтверждена - крепка как сталь!" + RESET);
                case "GOST":
                    System.out.println(WHITE + "✅ Подпись проверена - соответствует ГОСТу!" + RESET);
                    break;
            }
        } else {
            System.out.println(RED + "❌ Подпись недействительна!" + RESET);
        }
    }

    private static void printFinalResults(boolean elGamal, boolean rsa, boolean gost) {
        System.out.println(WHITE + "📊 ИТОГИ ПРОВЕРКИ:" + RESET);
        System.out.println(BLUE + "┌───────────────────┬──────────────────┐");
        System.out.println("│     Алгоритм      │     Статус      │");
        System.out.println("├───────────────────┼──────────────────┤");
        System.out.printf("│      ElGamal      │   %s   │\n",
                elGamal ? WHITE + "✅ УСПЕХ" + BLUE : RED + "❌ ОШИБКА" + BLUE);
        System.out.printf("│        RSA         │   %s   │\n",
                rsa ? WHITE + "✅ УСПЕХ" + BLUE : RED + "❌ ОШИБКА" + BLUE);
        System.out.printf("│        GOST        │   %s   │\n",
                gost ? WHITE + "✅ УСПЕХ" + BLUE : RED + "❌ ОШИБКА" + BLUE);
        System.out.println("└───────────────────┴──────────────────┘" + RESET);

        if (elGamal && rsa && gost) {
            System.out.println(RED + "\n🎉 ВСЕ ПОДПИСИ ВЕРНЫ!" + RESET);
            System.out.println(WHITE + "💪 Данные под защитой Российской Федерации!" + RESET);
            System.out.println(BLUE + "🇷🇺 СЛАВА РОССИИ! 🇷🇺" + RESET);
        }

        System.out.println("\n" + WHITE + "Работа завершена с честью и достоинством!" + RESET);
    }

    private static void saveSignatureToFile(String filename, Object signature) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(signature);
        }
    }
}