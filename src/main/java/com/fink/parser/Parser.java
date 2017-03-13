package com.fink.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Parser {

    private static final String ENCODING = "WINDOWS-1251";
    private static final String TEMP_FILE = "temp.txt";
    private static final String LANGUAGE = "Russian";

    public static boolean run(String input, File output) {
        try (OutputStream out = new FileOutputStream(TEMP_FILE)) {
            byte[] b = input.getBytes(ENCODING);
            out.write(b);
        } catch (IOException ex) {
            return false;
        }
        String rml = System.getenv("RML");
        if (rml == null) {
            return false;
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(rml+"Bin/TestSynan", LANGUAGE, TEMP_FILE);
            processBuilder.redirectOutput(output);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            return false;
        }
        return true;
    }
}
