package webapp.task2.service;

import webapp.task2.model.AccountSheet;
import webapp.task2.model.FinancialClassification;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SheetSummaryCreatorService {

    public List<AccountSheet> collectReport(List<AccountSheet> accounts) {
        List<AccountSheet> twoDigitAccounts = createTwoDigitSummary(accounts);
        List<AccountSheet> accountsByClass = createSummaryByClass(accounts);
        AccountSheet balance = createBalanceSummary(accountsByClass);
        return collectReport(accounts, twoDigitAccounts, accountsByClass, balance);
    }

    private final Comparator<AccountSheet> comparator = (o1, o2) -> {
        int number1 = Integer.parseInt(o1.getAccountNumber());
        int number2 = Integer.parseInt(o2.getAccountNumber());
        return number1 - number2;
    };

    private List<AccountSheet> collectReport(List<AccountSheet> accountSheets,
                                             List<AccountSheet> twoDigitAccounts,
                                             List<AccountSheet> accountsByClass,
                                             AccountSheet balance) {
        List<AccountSheet> report = new ArrayList<>();
        accountSheets.sort(comparator);
        twoDigitAccounts.sort(comparator);
        accountsByClass.sort(comparator);
        for (AccountSheet account : accountSheets) {
            if (!report.isEmpty()) {
                String lastAddedDigitNumber = getTwoDigitNumber(report.get(report.size() - 1));
                String currentDigitNumber = getTwoDigitNumber(account);
                if (!currentDigitNumber.equals(lastAddedDigitNumber)) {
                    report.add(twoDigitAccounts.remove(0));
                }
                FinancialClassification lastClassification = report.get(report.size() - 1).getClassification();
                FinancialClassification currentClassification = account.getClassification();
                if (!lastClassification.equals(currentClassification)) {
                    report.add(accountsByClass.remove(0));
                }
            }
            report.add(account);
        }
        report.add(twoDigitAccounts.remove(0));
        report.add(accountsByClass.remove(0));
        report.add(balance);
        return report;
    }

    private String getTwoDigitNumber(AccountSheet accountSheet) {
        return accountSheet.getAccountNumber().substring(0, 2);
    }

    public List<AccountSheet> createTwoDigitSummary(List<AccountSheet> accounts) {
        List<AccountSheet> twoDigitAccounts = new ArrayList<>();
        for (AccountSheet account : accounts) {
            String twoDigitAccountNumber = account.getAccountNumber().substring(0, 2);
            Optional<AccountSheet> accountSheet = twoDigitAccounts.stream()
                    .filter(a -> a.getAccountNumber().equals(twoDigitAccountNumber))
                    .findAny();

            if (accountSheet.isEmpty()) {
                AccountSheet twoDigitAcc = createNewAccount(account, twoDigitAccountNumber);
                twoDigitAccounts.add(twoDigitAcc);
            } else {
                calculateSummary(account, accountSheet.get());
            }
        }
        return twoDigitAccounts;
    }

    private AccountSheet createNewAccount(AccountSheet account, String accNumber) {
        AccountSheet twoDigitAcc = new AccountSheet();
        twoDigitAcc.setAccountNumber(accNumber);
        twoDigitAcc.setClassification(account.getClassification());
        calculateSummary(account, twoDigitAcc);
        return twoDigitAcc;
    }

    private void calculateSummary(AccountSheet account, AccountSheet summaryKeeper) {
        summaryKeeper.setActiveOpeningBalance(summaryKeeper.getActiveOpeningBalance()
                .add(account.getActiveOpeningBalance()));
        summaryKeeper.setPassiveOpeningBalance(summaryKeeper.getPassiveOpeningBalance()
                .add(account.getPassiveOpeningBalance()));
        summaryKeeper.setDebitTurnover(summaryKeeper.getDebitTurnover()
                .add(account.getDebitTurnover()));
        summaryKeeper.setDebitCredit(summaryKeeper.getDebitCredit()
                .add(account.getDebitCredit()));
        summaryKeeper.setActiveClosingBalance(summaryKeeper.getActiveClosingBalance()
                .add(account.getActiveClosingBalance()));
        summaryKeeper.setPassiveClosingBalance(summaryKeeper.getPassiveClosingBalance()
                .add(account.getPassiveClosingBalance()));
    }

    public List<AccountSheet> createSummaryByClass(List<AccountSheet> accounts) {
        List<AccountSheet> summaryByClass = new ArrayList<>();
        for (AccountSheet account : accounts) {
            Optional<AccountSheet> accountSheet = summaryByClass.stream()
                    .filter(a -> a.getClassification().equals(account.getClassification()))
                    .findAny();

            if (accountSheet.isEmpty()) {
                String classificationNumber = String.valueOf(account.getClassification().ordinal() + 1);
                AccountSheet accountByClassification = createNewAccount(account, classificationNumber);
                summaryByClass.add(accountByClassification);
            } else {
                calculateSummary(account, accountSheet.get());
            }
        }
        return summaryByClass;
    }

    public AccountSheet createBalanceSummary(List<AccountSheet> accounts) {
        AccountSheet balanceSummary = new AccountSheet();
        balanceSummary.setClassification(FinancialClassification.OTHER);
        balanceSummary.setAccountNumber("Баланс");
        for (AccountSheet account : accounts) {
            calculateSummary(account, balanceSummary);
        }
        return balanceSummary;
    }
}
