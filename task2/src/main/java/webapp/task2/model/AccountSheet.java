package webapp.task2.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class AccountSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private FinancialClassification classification;
    private String accountNumber;
    private BigDecimal activeOpeningBalance;
    private BigDecimal passiveOpeningBalance;
    private BigDecimal debitTurnover;
    private BigDecimal debitCredit;
    private BigDecimal activeClosingBalance;
    private BigDecimal passiveClosingBalance;

    @ManyToOne(fetch = FetchType.EAGER)
    private BankBalanceStatement balanceStatement;

    public AccountSheet() {
        activeOpeningBalance = BigDecimal.ZERO;
        passiveOpeningBalance = BigDecimal.ZERO;
        debitTurnover = BigDecimal.ZERO;
        debitCredit = BigDecimal.ZERO;
        activeClosingBalance = BigDecimal.ZERO;
        passiveClosingBalance = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FinancialClassification getClassification() {
        return classification;
    }

    public void setClassification(FinancialClassification classification) {
        this.classification = classification;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getActiveOpeningBalance() {
        return activeOpeningBalance;
    }

    public void setActiveOpeningBalance(BigDecimal activeOpeningBalance) {
        this.activeOpeningBalance = activeOpeningBalance;
    }

    public BigDecimal getPassiveOpeningBalance() {
        return passiveOpeningBalance;
    }

    public void setPassiveOpeningBalance(BigDecimal passiveOpeningBalance) {
        this.passiveOpeningBalance = passiveOpeningBalance;
    }

    public BigDecimal getDebitTurnover() {
        return debitTurnover;
    }

    public void setDebitTurnover(BigDecimal debitTurnover) {
        this.debitTurnover = debitTurnover;
    }

    public BigDecimal getDebitCredit() {
        return debitCredit;
    }

    public void setDebitCredit(BigDecimal debitCredit) {
        this.debitCredit = debitCredit;
    }

    public BigDecimal getActiveClosingBalance() {
        return activeClosingBalance;
    }

    public void setActiveClosingBalance(BigDecimal activeClosingBalance) {
        this.activeClosingBalance = activeClosingBalance;
    }

    public BigDecimal getPassiveClosingBalance() {
        return passiveClosingBalance;
    }

    public void setPassiveClosingBalance(BigDecimal passiveClosingBalance) {
        this.passiveClosingBalance = passiveClosingBalance;
    }

    public BankBalanceStatement getBalanceStatement() {
        return balanceStatement;
    }

    public void setBalanceStatement(BankBalanceStatement balanceStatement) {
        this.balanceStatement = balanceStatement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountSheet that = (AccountSheet) o;
        return Objects.equals(id, that.id)
                && classification == that.classification
                && Objects.equals(accountNumber, that.accountNumber)
                && Objects.equals(activeOpeningBalance, that.activeOpeningBalance)
                && Objects.equals(passiveOpeningBalance, that.passiveOpeningBalance)
                && Objects.equals(debitTurnover, that.debitTurnover)
                && Objects.equals(debitCredit, that.debitCredit)
                && Objects.equals(activeClosingBalance, that.activeClosingBalance)
                && Objects.equals(passiveClosingBalance, that.passiveClosingBalance)
                && Objects.equals(balanceStatement, that.balanceStatement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classification, accountNumber, activeOpeningBalance, passiveOpeningBalance, debitTurnover, debitCredit, activeClosingBalance, passiveClosingBalance, balanceStatement);
    }
}
