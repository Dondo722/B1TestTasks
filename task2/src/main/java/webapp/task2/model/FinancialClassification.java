package webapp.task2.model;

public enum FinancialClassification {
    CASH_AND_METALS("КЛАСС  1  Денежные средства, драгоценные металлы и межбанковские операции"),
    CREDIT_OPERATION("КЛАСС  2  Кредитные и иные активные операции с клиентами"),
    TRANSACTION_ACCOUNTS("КЛАСС  3  Счета по операциям клиентов"),
    SECURITIES("КЛАСС  4  Ценные бумаги"),
    LONG_TERM_INVESTMENTS_AND_PROPERTY("КЛАСС  5  Долгосрочные финансовые вложения в уставные фонды юридических " +
            "лиц, основные средства и прочее имущество"),
    ASSETS_AND_LIABILITIES("КЛАСС  6  Прочие активы и прочие пассивы"),
    BANK_CAPITAL("КЛАСС  7  Собственный капитал банка"),
    BANK_INCOME("КЛАСС  8  Доходы банка"),
    BANK_EXPENSES("КЛАСС  9  Расходы банка"),
    OTHER("КЛАСС 0 Другое");

    private final String denomination;

    FinancialClassification(String denomination) {
        this.denomination = denomination;
    }

    public String getDenomination() {
        return denomination;
    }
}
