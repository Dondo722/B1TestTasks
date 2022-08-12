package webapp.task2.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import webapp.task2.model.AccountSheet;
import webapp.task2.model.BankBalanceStatement;
import webapp.task2.model.FinancialClassification;
import webapp.task2.repository.AccountSheetRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class XlsDocWriterService {
    private final AccountSheetRepository accountRepository;
    private final SheetSummaryCreatorService summaryCreatorService;

    public XlsDocWriterService(AccountSheetRepository accountRepository,
                               SheetSummaryCreatorService summaryCreatorService) {
        this.accountRepository = accountRepository;
        this.summaryCreatorService = summaryCreatorService;
    }

    public void writeIntoExcel(File file, BankBalanceStatement statement) throws IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Sheet1");
        writeHeaderOfTable(statement, book, sheet);
        List<AccountSheet> accounts = accountRepository.findByBalanceStatement(statement);
        List<AccountSheet> report = summaryCreatorService.collectReport(accounts);
        writeTableBody(sheet, report);
        autosizeRows(sheet);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            book.write(stream);
        } finally {
            book.close();
        }
    }

    private void writeTableBody(Sheet sheet, List<AccountSheet> report) {
        FinancialClassification currentClassification = FinancialClassification.OTHER;
        int currentRowIndex = 8;
        for (AccountSheet accountSheet : report) {
            FinancialClassification accountClassification = accountSheet.getClassification();
            if (classificationChanged(currentClassification, accountClassification)) {
                writeClassificationRow(sheet, currentRowIndex, accountClassification);
                currentRowIndex++;
                currentClassification = accountClassification;
            }
            Row row = sheet.createRow(currentRowIndex);
            fillRowWithAccountValues(accountSheet, row);
            currentRowIndex++;
        }
    }

    private void writeHeaderOfTable(BankBalanceStatement statement, Workbook book, Sheet sheet) {
        // Нумерация начинается с нуля
        writeFirstRow(statement, sheet);
        writeSecondRow(sheet);
        writeThirdRow(statement, sheet);
        writeFourthRow(sheet);
        writeFifthRow(statement, book, sheet);
        writeSixthRow(sheet);
        writeSevenRow(sheet);
    }

    private boolean classificationChanged(FinancialClassification currentClassification, FinancialClassification accountClassification) {
        return accountClassification != currentClassification
                && accountClassification != FinancialClassification.OTHER;
    }

    private void writeClassificationRow(Sheet sheet, int currentRowIndex, FinancialClassification accountClassification) {
        Row row = sheet.createRow(currentRowIndex);
        writeIRowAndMerge(sheet, row, 0, accountClassification.getDenomination(),
                new CellRangeAddress(currentRowIndex, currentRowIndex, 0, 6));
    }

    private void fillRowWithAccountValues(AccountSheet accountSheet, Row row) {
        writeIRowJCell(row, 0, accountSheet.getAccountNumber());
        writeIRowJCell(row, 1, accountSheet.getActiveOpeningBalance().toString());
        writeIRowJCell(row, 2, accountSheet.getPassiveOpeningBalance().toString());
        writeIRowJCell(row, 3, accountSheet.getDebitTurnover().toString());
        writeIRowJCell(row, 4, accountSheet.getDebitCredit().toString());
        writeIRowJCell(row, 5, accountSheet.getActiveClosingBalance().toString());
        writeIRowJCell(row, 6, accountSheet.getPassiveClosingBalance().toString());
    }

    private void autosizeRows(Sheet sheet) {
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }

    private void writeFirstRow(BankBalanceStatement statement, Sheet sheet) {
        writeIRowAndMerge(sheet, sheet.createRow(0), 0, statement.getBankName(),
                new CellRangeAddress(0, 0, 0, 1));
    }

    private void writeSevenRow(Sheet sheet) {
        Row row = sheet.createRow(7);
        writeIRowJCell(row, 1, "Актив");
        writeIRowJCell(row, 2, "Пассив");
        writeIRowJCell(row, 3, "Дебет");
        writeIRowJCell(row, 4, "Кредит");
        writeIRowJCell(row, 5, "Актив");
        writeIRowJCell(row, 6, "Пассив");
    }

    private void writeSixthRow(Sheet sheet) {
        Row row = sheet.createRow(6);
        writeIRowAndMerge(sheet, row, 0,
                "Б/сч", new CellRangeAddress(6, 7, 0, 0));
        writeIRowAndMerge(sheet, row, 1,
                "ВХОДЯЩЕЕ САЛЬДО", new CellRangeAddress(6, 6, 1, 2));
        writeIRowAndMerge(sheet, row, 3,
                "ОБОРОТЫ", new CellRangeAddress(6, 6, 3, 4));
        writeIRowAndMerge(sheet, row, 5,
                "ИСХОДЯЩЕЕ САЛЬДО", new CellRangeAddress(6, 6, 5, 6));
    }

    private void writeFifthRow(BankBalanceStatement statement, Workbook book, Sheet sheet) {
        Row row5 = sheet.createRow(5);
        Cell creationDate = row5.createCell(0);
        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd/mm/yyyy hh:mm:ss"));
        creationDate.setCellStyle(dateStyle);
        creationDate.setCellValue(statement.getSheetCreation());
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1));
        Cell currency = row5.createCell(6);
        currency.setCellValue(statement.getCurrency());
    }

    private void writeFourthRow(Sheet sheet) {
        writeIRowAndMerge(sheet, sheet.createRow(3), 0,
                "по банку", new CellRangeAddress(3, 3, 0, 6));
    }

    private void writeThirdRow(BankBalanceStatement statement, Sheet sheet) {
        Row row3 = sheet.createRow(2);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Cell period = row3.createCell(0);
        String startRecord = formatter.format(statement.getStartRecord());
        String endRecord = formatter.format(statement.getEndRecord());
        period.setCellValue(String.format("за период с %s по %s", startRecord, endRecord));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
    }

    private void writeSecondRow(Sheet sheet) {
        writeIRowAndMerge(sheet, sheet.createRow(1), 0, "Оборотная ведомость по балансовым счетам",
                new CellRangeAddress(1, 1, 0, 6));
    }

    private void writeIRowAndMerge(Sheet sheet, Row row, int jCell, String value, CellRangeAddress rangeAddress) {
        writeIRowJCell(row, jCell, value);
        sheet.addMergedRegion(rangeAddress);
    }

    private void writeIRowJCell(Row row, int jCell, String value) {
        Cell cell = row.createCell(jCell);
        cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
        cell.setCellValue(value);
    }
}
