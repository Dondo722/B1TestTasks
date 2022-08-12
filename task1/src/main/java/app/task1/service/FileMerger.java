package app.task1.service;

import app.task1.util.LoadingBar;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static app.task1.util.PropertyKeeper.*;

public class FileMerger {
    private static final File MERGED = new File(DIRECTORY_SAVES_PATH, "merged.txt");

    public int mergeFilesWithoutSymbols(String symbols) throws IOException {
        List<String> allFilesLines = new ArrayList<>();
        if (MERGED.exists()) {
            MERGED.delete();
        }
        return mergeFilesWithoutSymbolsLogic(symbols, allFilesLines);
    }

    private int mergeFilesWithoutSymbolsLogic(String symbols, List<String> allFilesLines) throws IOException {
        int totalSymbolsAppearanceCounter = 0;
        File directory = new File(DIRECTORY_SAVES_PATH);
        if (!directory.exists()) {
            throw new NoSuchFileException("Directory is not exist");
        }
        File[] files = directory.listFiles();
        LoadingBar loadingBar = new LoadingBar(files.length);
        for (File file : files) {
            loadingBar.loadingProcess();
            totalSymbolsAppearanceCounter = countSymbolAppearanceAndAddingLinesFileLines(symbols, allFilesLines,
                    totalSymbolsAppearanceCounter, file);
        }
        fillMergedFile(allFilesLines);
        return totalSymbolsAppearanceCounter;
    }

    private int countSymbolAppearanceAndAddingLinesFileLines(String symbols, List<String> allFilesLines,
                                                             int totalSymbolsAppearanceCounter, File file)
            throws IOException {
        List<String> oneFileLines = new ArrayList<>();
        int fileSymbolsAppearanceCounter = 0;
        FileReader reader = new FileReader(file);
        try (Scanner scanner = new Scanner(reader)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(symbols)) {
                    fileSymbolsAppearanceCounter++;
                } else {
                    oneFileLines.add(line);
                }
            }
        }
        if (fileSymbolsAppearanceCounter != 0) {
            totalSymbolsAppearanceCounter += fileSymbolsAppearanceCounter;
            fillTheFile(file,oneFileLines);
        }
        allFilesLines.addAll(oneFileLines);
        return totalSymbolsAppearanceCounter;
    }

    private void fillMergedFile(List<String> allFilesLines) throws IOException {
        MERGED.createNewFile();
        fillTheFile(MERGED,allFilesLines);
    }

    private void fillTheFile(File file, List<String> lines) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file,false)) {
            for (String line : lines) {
                fileWriter.write(line);
                fileWriter.write(LINE_SEPARATOR);
            }
        }
    }


}
