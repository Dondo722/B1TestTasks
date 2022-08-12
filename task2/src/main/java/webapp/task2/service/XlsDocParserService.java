package webapp.task2.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import webapp.task2.model.AccountSheet;
import webapp.task2.model.BankBalanceStatement;
import webapp.task2.model.FinancialClassification;
import webapp.task2.service.util.DateInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XlsDocParserService {

    public BankBalanceStatement parseDocStatement(File file) throws UnableToParseDocumentException {
        try (Workbook book = new HSSFWorkbook(new FileInputStream(file))) {
            Sheet excelSheet = book.getSheetAt(0);
            BankBalanceStatement statement = parseBankBalanceStatement(excelSheet);
            statement.setFileName(file.getName());
            return statement;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new UnableToParseDocumentException("Please check the document");
        }
    }

    private BankBalanceStatement parseBankBalanceStatement(Sheet sheet) throws ParseException {
        Row row = sheet.getRow(0);
        String bankName = row.getCell(0).getStringCellValue();
        Row statementPeriodRow = sheet.getRow(2);
        String statementPeriod = statementPeriodRow.getCell(0).getStringCellValue();
        Row sheetCreationRow = sheet.getRow(5);
        Date sheetCreation = sheetCreationRow.getCell(0).getDateCellValue();
        String statementCurrency = sheetCreationRow.getCell(6).getStringCellValue();
        Date startRecord = parseDate(statementPeriod, DateInfo.START_DATE);
        Date endRecord = parseDate(statementPeriod, DateInfo.END_DATE);
        return fillBalanceStatement(bankName, sheetCreation, startRecord, endRecord, statementCurrency);
    }

    private BankBalanceStatement fillBalanceStatement(String bankName, Date sheetCreation, Date startRecord,
                                                      Date endRecord, String statementCurrency) {
        BankBalanceStatement bankBalanceStatement = new BankBalanceStatement();
        bankBalanceStatement.setBankName(bankName);
        bankBalanceStatement.setSheetCreation(sheetCreation);
        bankBalanceStatement.setStartRecord(startRecord);
        bankBalanceStatement.setEndRecord(endRecord);
        bankBalanceStatement.setCurrency(statementCurrency);
        return bankBalanceStatement;
    }

    private Date parseDate(String statementPeriod, DateInfo info) throws ParseException {
        String datesOutOfString = statementPeriod.replaceAll("[^ .,a-zA-Z0-9]", " ");
        String[] statementDates = datesOutOfString.trim().split(" ");

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        if (info == DateInfo.START_DATE) {
            return formatter.parse(statementDates[0]);
        } else {
            return formatter.parse(statementDates[statementDates.length - 1]);
        }
    }

    public List<AccountSheet> parseDocAccountSheets(File file) throws UnableToParseDocumentException {
        try (Workbook book = new HSSFWorkbook(new FileInputStream(file))) {
            List<AccountSheet> sheets = new ArrayList<>();
            Sheet excelSheet = book.getSheetAt(0);
            FinancialClassification classification = FinancialClassification.OTHER;
            for (int k = 8; k <= excelSheet.getLastRowNum(); k++) {
                Row row = excelSheet.getRow(k);
                if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
                    String cell = row.getCell(0).getStringCellValue();
                    if (isClassificationCell(cell)) {
                        classification = parseFinancialClassification(cell);
                    } else if (cell.matches("\\d+") && cell.length() > 2) {
                        AccountSheet accountSheet = parseAccountSheet(classification, row);
                        sheets.add(accountSheet);
                    }
                }
            }
            return sheets;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnableToParseDocumentException("Please check the document");
        }
    }

    private AccountSheet parseAccountSheet(FinancialClassification classification,
                                                  Row row) {
        String accountNumber = row.getCell(0).getStringCellValue();
        BigDecimal activeOpeningBalance = getBigDecimalCellValue(row, 1);
        BigDecimal passiveOpeningBalance = getBigDecimalCellValue(row, 2);
        BigDecimal debitTurnover = getBigDecimalCellValue(row, 3);
        BigDecimal debitCredit = getBigDecimalCellValue(row, 4);
        BigDecimal activeClosingBalance = getBigDecimalCellValue(row, 5);
        BigDecimal passiveClosingBalance = getBigDecimalCellValue(row, 6);

        return fillAccountSheet(classification, accountNumber, activeOpeningBalance,
                passiveOpeningBalance, debitTurnover, debitCredit, activeClosingBalance, passiveClosingBalance);
    }

    private AccountSheet fillAccountSheet(FinancialClassification classification, String accountNumber,
                                          BigDecimal activeOpeningBalance, BigDecimal passiveOpeningBalance,
                                          BigDecimal debitTurnover, BigDecimal debitCredit,
                                          BigDecimal activeClosingBalance, BigDecimal passiveClosingBalance) {
        AccountSheet accountSheet = new AccountSheet();
        accountSheet.setAccountNumber(accountNumber);
        accountSheet.setActiveOpeningBalance(activeOpeningBalance);
        accountSheet.setPassiveOpeningBalance(passiveOpeningBalance);
        accountSheet.setDebitTurnover(debitTurnover);
        accountSheet.setDebitCredit(debitCredit);
        accountSheet.setActiveClosingBalance(activeClosingBalance);
        accountSheet.setPassiveClosingBalance(passiveClosingBalance);
        accountSheet.setClassification(classification);
        return accountSheet;
    }

    private BigDecimal getBigDecimalCellValue(Row row, int i) {
        Cell cellValue = row.getCell(i);
        cellValue.setCellType(Cell.CELL_TYPE_STRING);
        return new BigDecimal(cellValue.getStringCellValue());
    }

    private FinancialClassification parseFinancialClassification(String value) {
        return switch (value.replaceAll("\\D", "")) {
            case "1" -> FinancialClassification.CASH_AND_METALS;
            case "2" -> FinancialClassification.CREDIT_OPERATION;
            case "3" -> FinancialClassification.TRANSACTION_ACCOUNTS;
            case "4" -> FinancialClassification.SECURITIES;
            case "5" -> FinancialClassification.LONG_TERM_INVESTMENTS_AND_PROPERTY;
            case "6" -> FinancialClassification.ASSETS_AND_LIABILITIES;
            case "7" -> FinancialClassification.BANK_CAPITAL;
            case "8" -> FinancialClassification.BANK_INCOME;
            case "9" -> FinancialClassification.BANK_EXPENSES;
            default -> FinancialClassification.OTHER;
        };
    }

    private boolean isClassificationCell(String cell) {
        return cell.matches("\\b(КЛАСС\\s*\\d+\\D*)\\b");
    }



}
