package webapp.task2.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@Entity
public class BankBalanceStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String bankName;
    private Date sheetCreation;
    private Date startRecord;
    private Date endRecord;
    private String fileName;
    private String currency;

    public BankBalanceStatement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getSheetCreation() {
        return sheetCreation;
    }

    public void setSheetCreation(Date sheetCreation) {
        this.sheetCreation = sheetCreation;
    }

    public Date getStartRecord() {
        return startRecord;
    }

    public void setStartRecord(Date startRecord) {
        this.startRecord = startRecord;
    }

    public Date getEndRecord() {
        return endRecord;
    }

    public void setEndRecord(Date endRecord) {
        this.endRecord = endRecord;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankBalanceStatement that = (BankBalanceStatement) o;
        return Objects.equals(id, that.id) && Objects.equals(bankName, that.bankName)
                && Objects.equals(sheetCreation, that.sheetCreation) && Objects.equals(startRecord, that.startRecord)
                && Objects.equals(endRecord, that.endRecord) && Objects.equals(fileName, that.fileName)
                && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bankName, sheetCreation, startRecord, endRecord, fileName, currency);
    }
}
