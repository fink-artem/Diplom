package com.fink.ontology;

import java.io.File;
import java.io.IOException;

public class SyntaxParser {

    private static final String out = "./output.txt";

    static File parse(File f) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("./parser/Parser.exe", "-tagger", "0", "-parser", "0", "-lemmatizer", "0", "-emit_morph", "0", "-eol", "-d", "./parser/dictionary.xml", f.getPath(), "-o", out);
        processBuilder.redirectOutput(new File("./error.txt"));
        Process process = processBuilder.start();
        process.waitFor();
        return new File(out);
    }
}
