package app.task1.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class StringGenerator {
    private static final String DELIMITER = "||";

    public String generateString() {
        StringBuilder builder = new StringBuilder();
        String formatDate = randomDate();
        builder.append(formatDate).append(DELIMITER);
        char[] englishWord = getRandomWord(this::randomEnglishChar);
        builder.append(englishWord).append(DELIMITER);
        char[] russianWord = getRandomWord(this::randomRussianChar);
        builder.append(russianWord).append(DELIMITER);
        builder.append(randomPositiveInteger()).append(DELIMITER);
        builder.append(randomPositiveDouble()).append(DELIMITER);
        return builder.toString();
    }

    private char[] getRandomWord(Supplier<Character> supplier) {
        char[] englishWord = new char[10];
        for (int i = 0; i < 10; i++) {
            englishWord[i] = supplier.get();
        }
        return englishWord;
    }

    private String randomDate() {
        long random = ThreadLocalRandom.current().nextLong(
                new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime().getTime(),
                System.currentTimeMillis());
        Date date = new Date(random);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }

    private char randomEnglishChar() {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);
    }

    private char randomRussianChar() {
        int rnd = (int) (Math.random() * 64); // or use Random or whatever
        char base = (rnd < 32) ? 'А' : 'а';
        return (char) (base + rnd % 32);
    }

    private int randomPositiveInteger() {
        int randomEvenInt = (int) (Math.random() * 100_000_000);
        if ((randomEvenInt % 2 == 1)) {
            randomEvenInt--;
        }
        return randomEvenInt;
    }

    private String randomPositiveDouble() {
        double randomEvenDouble = Math.random() * 20;
        return String.format("%.8f", randomEvenDouble);
    }

}
