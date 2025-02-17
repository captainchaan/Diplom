package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DataHelper {
    private static Faker faker = new Faker(new Locale("en"));
    private static Faker fakerRus = new Faker(new Locale("ru"));

    private DataHelper() {
    }

    public static CardInfo getCardApproved() {
        return new CardInfo("4444 4444 4444 4441", "APPROVED");

    }

    public static CardInfo getCardDeclined() {

        return new CardInfo("4444 4444 4444 4442", "DECLINED");
    }

    public static String getRandomCardNumber() {
        return faker.business().creditCardNumber();

    }

    public static String GetAShortNumber() {
        int shortNumber = faker.random().nextInt(16);
        return faker.number().digits(shortNumber);
    }

    public static String getRandomMonth(int month) {
        return LocalDate.now().plusMonths(month).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getRandomYear(int year) {
        return LocalDate.now().plusYears(year).format(DateTimeFormatter.ofPattern("yy"));

    }

    public static String getInvalidTwo() {

        return "00";
    }
    public static String getInvalidOne() {

        return "0";
    }

    public static String getRandomName() {

        return faker.name().fullName();
    }

    public static String getRandomNameRus() {

        return fakerRus.name().fullName();
    }

    public static String getNumberName() {

        return faker.number().digit();
    }

    public static String getNumberCVC(int code) {

        return faker.number().digits(code);
    }

    public static String getSpecialSymbols() {

        return "&^^%&^%*&^&&&&&&";
    }
    public static String getSpecialLetters() {

        return "tyfjcvbl";
    }

    public static String getEmptyField() {
        return "";
    }
    public static String getCardNoBase() {

        return "4444 4444 4444 4443";
    }

    @Value
    public static class CardInfo {
        public String cardNumber;
        public String Status;

    }

}
