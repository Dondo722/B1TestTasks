package app.task1;

import app.task1.model.CompletedString;
import app.task1.service.FileGenerator;
import app.task1.service.FileMerger;
import app.task1.util.LoadingBar;
import org.postgresql.util.PSQLException;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static app.task1.util.PropertyKeeper.DIRECTORY_SAVES_PATH;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, SQLException, ParseException {
        Properties prop = getProperties();
        String url = prop.getProperty("DB_URL");
        String user = prop.getProperty("DB_USER");
        String password = prop.getProperty("DB_PASSWORD");
        Connection conn = DriverManager.getConnection(url, user, password);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            String input = scanner.nextLine();
            try {
                switch (input) {
                    case "1":
                        FileGenerator generator = new FileGenerator();
                        generator.createFiles();
                        break;
                    case "2":
                        FileMerger merger = new FileMerger();
                        System.out.println("Введите сочетание символов, которые хотите удалить:");//todo
                        String symbols = scanner.nextLine();
                        int linesWithSymbol = merger.mergeFilesWithoutSymbols(symbols);
                        System.out.println(linesWithSymbol + " строк удалено");
                        break;
                    case "3":
                        createTableIfNotExists(conn);
                        File file = chooseFile(scanner);
                        List<CompletedString> completedStrings = parseDataToWrapperClass(file);
                        importToDMBS(conn, completedStrings);
                        break;
                    case "4":
                        countAndPrintSumAndMedian(conn);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Попробуйте ввести ещё раз");
                }
            } catch (NoSuchFileException e) {
                System.out.println("Сначала нужно сгенерировать файлы, чтобы потом их объединить");
            } catch (PSQLException e) {
                System.out.println("Сначала нужно импортировать данные в СУБД");
            }
        }
    }

    private static void countAndPrintSumAndMedian(Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            String sql = "select sum(lt2.positive_even_integer) as s, avg(s.positive_double) as a\n" +
                    "from (select *,row_number() over (order by positive_double desc) as desc_double,\n" +
                    "row_number() over (order by positive_double )     as asc_double\n" +
                    "from lines_test2) as s,\n" +
                    "lines_test2 as lt2\n" +
                    "where asc_double in (desc_double,desc_double+1,desc_double-1);";
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            System.out.print("Сумма: ");
            System.out.println(resultSet.getString("s"));
            System.out.print("Медиана: ");
            System.out.println(resultSet.getString("a"));
        }
    }

    private static void printMenu() {
        System.out.println("1) Сгенерировать файлы");
        System.out.println("2) Объединить файлы");
        System.out.println("3) Импортировать в СУБД");
        System.out.println("4) Сумма и Медиана");
        System.out.println("0) Завершить работу");
    }

    private static void importToDMBS(Connection conn, List<CompletedString> completedStrings) throws SQLException, ParseException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "insert into lines_test2 (random_date, latin, russian, positive_even_integer, positive_double)\n" +
                        "values (?,?,?,?,?)  ")) {
            LoadingBar loadingBar = new LoadingBar(completedStrings.size());
            for (CompletedString s : completedStrings) {
                loadingBar.loadingOutOf();
                prepareStatement(preparedStatement, s);
                preparedStatement.executeUpdate();
            }
        }
    }

    private static void prepareStatement(PreparedStatement preparedStatement, CompletedString s)
            throws SQLException, ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        preparedStatement.setDate(1, new Date(formatter.parse(s.getDate()).getTime()));
        preparedStatement.setString(2, s.getLatinSymbols());
        preparedStatement.setString(3, s.getRussianSymbols());
        preparedStatement.setInt(4,Integer.parseInt(s.getPositiveEvenNumber()));
        preparedStatement.setFloat(5,
                Float.parseFloat(s.getPositiveDoubleNumber().replace(',','.')));
    }

    private static List<CompletedString> parseDataToWrapperClass(File file) throws FileNotFoundException {
        FileReader rdr = new FileReader(file);
        List<CompletedString> completedStrings = new ArrayList<>();
        try (Scanner s = new Scanner(rdr)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] data = line.split("\\|\\|");
                CompletedString completedLine =
                        new CompletedString(data[0],data[1],data[2],data[3],data[4]);
                completedStrings.add(completedLine);
            }
        }
        return completedStrings;
    }

    private static File chooseFile(Scanner scanner) {
        System.out.println("Введите имя файла из директории saves для импорта");
        String fileName = scanner.nextLine();
        if (!fileName.contains(".txt")) {
            fileName = fileName + ".txt";
        }
        File file = new File(DIRECTORY_SAVES_PATH, fileName);
        return file;
    }

    private static void createTableIfNotExists(Connection conn) throws SQLException {
        if (!tableExists(conn,"lines_test2")) {
            try (Statement statement = conn.createStatement()) {
                String sql = "create table lines_test2 (\n" +
                        "\trandom_date date,\n" +
                        "\tlatin varchar(10),\n" +
                        "\trussian varchar(10),\n" +
                        "\tpositive_even_integer int,\n" +
                        "\tpositive_double float(24)\n" +
                        ");";
                statement.executeUpdate(sql);
            }
        }
    }

    private static Properties getProperties() throws IOException {
        Properties prop = new Properties();
        String propFileName = "config.properties";
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        return prop;
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});

        return resultSet.next();
    }
}
