package webapp.task2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import webapp.task2.repository.AccountSheetRepository;
import webapp.task2.service.SheetSummaryCreatorService;
import webapp.task2.service.XlsDocParserService;
import webapp.task2.service.XlsDocWriterService;

import javax.servlet.MultipartConfigElement;

@Configuration
@SpringBootApplication
public class ApplicationConfiguration {

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("1024KB"));
        factory.setMaxRequestSize(DataSize.parse("1024KB"));
        return factory.createMultipartConfig();
    }

    @Bean
    XlsDocParserService parser() {
        return new XlsDocParserService();
    }

    @Bean
    SheetSummaryCreatorService summaryCreator() {
        return new SheetSummaryCreatorService();
    }

    @Bean
    XlsDocWriterService writer(AccountSheetRepository repo, SheetSummaryCreatorService summaryCreator) {
        return new XlsDocWriterService(repo, summaryCreator);
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

}
