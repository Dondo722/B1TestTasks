package app.task1.service;

import app.task1.util.LoadingBar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static app.task1.util.PropertyKeeper.LINE_SEPARATOR;

public class FileFiller implements Runnable{

    private final int firstIndex;
    private final int lastIndex;
    private final String directoryPath;

    public FileFiller(int firstIndex, int lastIndex, String directoryPath) {
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.directoryPath = directoryPath;
    }

    @Override
    public void run() {
        try {
            StringGenerator stringGenerator = new StringGenerator();
            LoadingBar loadingBar = new LoadingBar(lastIndex - firstIndex);
            for (int i = firstIndex; i < lastIndex; i++) {
                loadingBar.loadingProcess();
                File file = new File(directoryPath, i + ".txt");
                file.createNewFile();
                try (FileWriter fileWriter = new FileWriter(file)) {
                    for (int j = 0; j < 100_000; j++) {
                        String line = stringGenerator.generateString();
                        fileWriter.write(line);
                        fileWriter.write(LINE_SEPARATOR);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
