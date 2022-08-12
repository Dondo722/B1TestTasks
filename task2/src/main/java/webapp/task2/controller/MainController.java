package webapp.task2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webapp.task2.model.AccountSheet;
import webapp.task2.model.BankBalanceStatement;
import webapp.task2.repository.AccountSheetRepository;
import webapp.task2.repository.BalanceStatementRepository;
import webapp.task2.service.SheetSummaryCreatorService;
import webapp.task2.service.UnableToParseDocumentException;
import webapp.task2.service.XlsDocParserService;
import webapp.task2.service.XlsDocWriterService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
public class MainController {
    private static final String FAIL_LOAD_FILE = "fail_load_file";
    private static final String PARSE_FAIL = "parse_fail";
    private static final String SUCCESS = "success";
    private static final String LOAD_FILE_FAIL_MESSAGE = "Please attach a .xls file";
    private static final String PARSE_FAIL_MESSAGE = "Can't parse this file, please check if it's composed correctly";
    private static final String FILE_UPLOADED_MESSAGE = "File " + SUCCESS + "fully uploaded!";

    private final XlsDocParserService parser;
    private final AccountSheetRepository accountSheetRepo;
    private final BalanceStatementRepository balanceStatementRepo;
    private final SheetSummaryCreatorService sheetSummaryCreator;
    private final XlsDocWriterService writer;

    public MainController(XlsDocParserService parser,
                          AccountSheetRepository accountSheetRepo,
                          BalanceStatementRepository balanceStatementRepo,
                          SheetSummaryCreatorService sheetSummaryCreator,
                          XlsDocWriterService writer) {
        this.parser = parser;
        this.accountSheetRepo = accountSheetRepo;
        this.balanceStatementRepo = balanceStatementRepo;
        this.sheetSummaryCreator = sheetSummaryCreator;
        this.writer = writer;
    }

    @GetMapping("/")
    public String homePage(@RequestParam(name = "u", required = false) String uploadStatus,
                           Model model) {
        if (SUCCESS.equals(uploadStatus)) {
            model.addAttribute("message", FILE_UPLOADED_MESSAGE);
        }
        return "homepage";
    }

    @GetMapping("/statements")
    public String statements(Model model) {
        model.addAttribute("statements", balanceStatementRepo.findAll());
        return "statements";
    }

    @GetMapping("/statements/{statement}")
    public String statementPage(@PathVariable BankBalanceStatement statement, Model model) {
        model.addAttribute("statement", statement);
        List<AccountSheet> accounts = accountSheetRepo.findByBalanceStatement(statement);
        List<AccountSheet> collectedAccountReport =
                sheetSummaryCreator.collectReport(accounts);
        model.addAttribute("accounts", collectedAccountReport);
        return "accounts";
    }

    @GetMapping("/save/{statement}")
    @ResponseBody
    public String save(@PathVariable BankBalanceStatement statement,
                       HttpServletResponse response) {
        try {
            File file = new File(statement.getFileName());
            file.createNewFile();
            writer.writeIntoExcel(file, statement);
            downloadFile(response, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "/statements/" + statement.getId();
    }

    @GetMapping("/upload")
    public String upload(@RequestParam(name = "u", required = false) String uploadFailure,
                         Model model) {
        String message = null;
        if (FAIL_LOAD_FILE.equals(uploadFailure)) {
            message = LOAD_FILE_FAIL_MESSAGE;
        } else if (PARSE_FAIL.equals(uploadFailure)) {
            message = PARSE_FAIL_MESSAGE;
        }
        model.addAttribute("messageFailure", message);
        return "upload";
    }

    @PostMapping(value = "/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        File uploadedFile = null;
        try {
            uploadedFile = saveMultipartFileAsFile(file);
            parseAndSaveUploadedData(uploadedFile);
            return "redirect:/?u=" + SUCCESS;
        } catch (UnableToParseDocumentException e) {
            e.printStackTrace();
            return "redirect:/upload?u=" + PARSE_FAIL;
        } catch (Throwable e) {
            e.printStackTrace();
            return "redirect:/upload?u=" + FAIL_LOAD_FILE;
        } finally {
            if (uploadedFile != null) {
                uploadedFile.delete();
            }
        }
    }

    private void downloadFile(HttpServletResponse response, File file) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        try (ServletOutputStream outputStream = response.getOutputStream();
             BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {

            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            file.delete();
        }
    }

    private File saveMultipartFileAsFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        File uploadedFile = new File(file.getOriginalFilename());
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile))) {
            stream.write(bytes);
        }
        return uploadedFile;
    }

    private void parseAndSaveUploadedData(File uploadedFile) throws UnableToParseDocumentException {
        BankBalanceStatement bankBalanceStatement = parser.parseDocStatement(uploadedFile);
        balanceStatementRepo.save(bankBalanceStatement);
        List<AccountSheet> sheets = parser.parseDocAccountSheets(uploadedFile);
        sheets.forEach(s -> {
            s.setBalanceStatement(bankBalanceStatement);
            accountSheetRepo.save(s);
        });
    }
}
