package app.task1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static app.task1.util.PropertyKeeper.*;

public class FileGenerator {

    public void createFiles() throws IOException, InterruptedException {
        createOrClearDirectory();
        fillFilesWithData();
    }

    private void fillFilesWithData() throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            int threadFirstIndex = i * 25;
            int threadLastIndex = threadFirstIndex + 25;
            es.execute(new FileFiller(threadFirstIndex,threadLastIndex, DIRECTORY_SAVES_PATH));
        }
        es.shutdown();
        boolean fillIsFinished = false;
        while (!fillIsFinished) {
            fillIsFinished = es.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    private void createOrClearDirectory() throws IOException {
        File directory = new File(DIRECTORY_SAVES_PATH);
        if (directory.exists()) {
            deleteAllFilesFolder(directory);
            directory.delete();
        }
        Files.createDirectory(Paths.get(DIRECTORY_SAVES_PATH));
    }

    public void deleteAllFilesFolder(File directory) {
        for (File myFile : Objects.requireNonNull(directory.listFiles())) {
            if (myFile.isFile()) {
                myFile.delete();
            }
        }
    }
}
