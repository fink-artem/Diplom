package com.fink.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Parser {

    private static final String ENCODING = "WINDOWS-1251";

    public static void run(String input, File output) throws IOException, InterruptedException {
        try (OutputStream out = new FileOutputStream("temp.txt")) {
            byte[] b = input.getBytes(ENCODING);
            out.write(b);
        } 
        ProcessBuilder processBuilder = new ProcessBuilder("./parser/Parser.exe", "-tagger", "0");
        Process process = processBuilder.start();
        process.waitFor();
    }
}
