package com.fink.parser;

import com.fink.logic.Link;
import com.fink.logic.Node;
import com.fink.logic.SemanRel;
import com.fink.logic.Sent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SemanParser {

    private static final String TEMP_FILE2 = "temp2.txt";
    private static final String RELATIONS = "Relations:";

    public static Sent run(String input) {
        String rml = System.getenv("RML");
        if (rml == null) {
            return null;
        }
        File f = new File(TEMP_FILE2);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(rml + "Bin/TestSeman");
            processBuilder.redirectInput(new File(input));
            processBuilder.redirectOutput(f);
            Process process = processBuilder.start();
            process.waitFor();
            List<Node> nodeList = new ArrayList<>();
            List<Link> linkList = new ArrayList<>();
            try (Scanner reader = new Scanner(f)) {
                reader.nextLine();
                reader.nextLine();
                while (true) {
                    String line = reader.nextLine();
                    if (line.equals(RELATIONS)) {
                        break;
                    }
                    nodeList.add(parseLine(line));
                }
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    linkList.add(parseLink(line));
                }
            } catch (FileNotFoundException ex) {
            }
            Sent sent = new Sent();
            sent.linkList = linkList;
            sent.nodeList = nodeList;
            return sent;
        } catch (InterruptedException | IOException ex) {
            return null;
        } 
    }
    

    private static Node parseLine(String line) {
        int search = line.indexOf(":") + 2;
        int searchSpace = line.indexOf(" ", search);
        Node node = new Node();
        node.name = line.substring(search, searchSpace);
        return node;
    }

    private static Link parseLink(String line) {
        int searchOpen = line.lastIndexOf("(");
        int searchClose = line.lastIndexOf(")");
        int searchComma = line.indexOf(",", searchOpen);
        Link link = new Link();
        link.type = SemanRel.convert((new Scanner(line)).next());
        link.firstNodeNumber = Integer.parseInt(line.substring(searchOpen + 1, searchComma));
        link.secondNodeNumber = Integer.parseInt(line.substring(searchComma + 2, searchClose));
        return link;
    }
}
