package app.task1.util;

public class PropertyKeeper {
    public static final String USER_DIRECTORY = System.getProperty("user.dir");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String DIRECTORY_SAVES_PATH = USER_DIRECTORY + FILE_SEPARATOR + "saves";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private PropertyKeeper() {}
}
