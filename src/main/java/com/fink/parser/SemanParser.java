package com.fink.parser;

import com.fink.logic.Link;
import com.fink.logic.Node;
import com.fink.logic.SemanRel;
import com.fink.logic.Sent;
import com.fink.logic.SyntaxRel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SemanParser {

    private static final String TEMP_FILE = "tempSeman.txt";
    private static final String TEMP_FILE2 = "temp2.txt";
    private static final String TEMP_FILE3 = "temp3.txt";
    private static final String RELATIONS = "Relations:";
    private static final String LANGUAGE = "Russian";

    public static List<Sent> run(String input) {
        /*String rml = System.getenv("RML");
         if (rml == null) {
         return null;
         }*/
        //try {
            /*ProcessBuilder processBuilder = new ProcessBuilder(rml + "Bin/GraphmatThick", LANGUAGE, input, "-sents", TEMP_FILE2);
         Process process = processBuilder.start();
         process.waitFor();*/
        List<Sent> sentList = new ArrayList<>();
        /*try (Scanner read = new Scanner(new File(TEMP_FILE2))) {
         //while (read.hasNext()) {
         File temp = new File(TEMP_FILE3);
         try(PrintWriter out = new PrintWriter(temp)){
         out.println(read.nextLine());
         }*/
        //File f = new File(TEMP_FILE);
        File f = new File("answer8.txt");
        /*processBuilder = new ProcessBuilder(rml + "Bin/TestSeman");
         processBuilder.redirectInput(temp);
         processBuilder.redirectOutput(f);
         process = processBuilder.start();
         process.waitFor();*/

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
        sentList.add(sent);
        //}
        //}
        return sentList;
        //} catch (InterruptedException | IOException ex) {
        //   return null;
        //}
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
        int searchequals = line.lastIndexOf("=", searchOpen);
        Link link = new Link();
        link.semanType = SemanRel.convert((new Scanner(line)).next());
        link.synanType = SyntaxRel.convert(line.substring(searchequals+2, searchOpen - 1).trim().toUpperCase());
        link.firstNodeNumber = Integer.parseInt(line.substring(searchOpen + 1, searchComma));
        link.secondNodeNumber = Integer.parseInt(line.substring(searchComma + 2, searchClose));
        return link;
    }
}
