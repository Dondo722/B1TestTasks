package app.task1.model;

import java.util.Objects;

public class CompletedString {
    private final String date;
    private final String latinSymbols;
    private final String russianSymbols;
    private final String positiveEvenNumber;
    private final String positiveDoubleNumber;

    public CompletedString(String date, String latinSymbols, String russianSymbols, String positiveEvenNumber,
                           String positiveDoubleNumber) {
        this.date = date;
        this.latinSymbols = latinSymbols;
        this.russianSymbols = russianSymbols;
        this.positiveEvenNumber = positiveEvenNumber;
        this.positiveDoubleNumber = positiveDoubleNumber;
    }

    public String getDate() {
        return date;
    }

    public String getLatinSymbols() {
        return latinSymbols;
    }

    public String getRussianSymbols() {
        return russianSymbols;
    }

    public String getPositiveEvenNumber() {
        return positiveEvenNumber;
    }

    public String getPositiveDoubleNumber() {
        return positiveDoubleNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletedString that = (CompletedString) o;
        return Objects.equals(date, that.date)
                && Objects.equals(latinSymbols, that.latinSymbols)
                && Objects.equals(russianSymbols, that.russianSymbols)
                && Objects.equals(positiveEvenNumber, that.positiveEvenNumber)
                && Objects.equals(positiveDoubleNumber, that.positiveDoubleNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, latinSymbols, russianSymbols, positiveEvenNumber, positiveDoubleNumber);
    }

    @Override
    public String toString() {
        return "CompletedString{" +
                "date='" + date + '\'' +
                ", latinSymbols='" + latinSymbols + '\'' +
                ", russianSymbols='" + russianSymbols + '\'' +
                ", positiveEvenNumber='" + positiveEvenNumber + '\'' +
                ", positiveDoubleNumber='" + positiveDoubleNumber + '\'' +
                '}';
    }
}
