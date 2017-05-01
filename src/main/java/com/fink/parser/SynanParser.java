package com.fink.parser;

import com.fink.logic.Rel;
import com.fink.logic.SyntaxRel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SynanParser {

    private static final String ENCODING = "WINDOWS-1251";
    private static final String TEMP_FILE2 = "temp2.txt";
    private static final String LANGUAGE = "Russian";
    private static final String SENTENCE = "sent";
    private static final String NAME = "name";
    private static final String GRAMMAR_CHILD = "grmchld";
    private static final String GRAMMAR_PARENT = "grmprnt";
    private static final String LEMMA_CHILD = "lemmchld";
    private static final String LEMMA_PARENT = "lemmprnt";
    private static final String RELATION = "rel";

    public static List<List<Rel>> run(String input) {
        String rml = System.getenv("RML");
        if (rml == null) {
            return null;
        }
        File f = new File(TEMP_FILE2);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(rml + "/Bin/TestSynan", LANGUAGE, input);
            processBuilder.redirectOutput(f);
            Process process = processBuilder.start();
            process.waitFor();

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputStore = new InputSource(new FileInputStream(f));
            inputStore.setEncoding(ENCODING);
            Document document = documentBuilder.parse(inputStore);
            Node root = document.getDocumentElement();
            NodeList nodeSecondLevelList = root.getChildNodes();

            List<List<Rel>> textSynan = new ArrayList<>();
            for (int i = 0; i < nodeSecondLevelList.getLength(); i++) {
                Node nodeSecondLevel = nodeSecondLevelList.item(i);
                if (nodeSecondLevel.getNodeType() != Node.TEXT_NODE && nodeSecondLevel.getNodeName().equals(SENTENCE)) {
                    textSynan.add(parseSent(nodeSecondLevel));
                }
            }
            return textSynan;
        } catch (InterruptedException | IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Rel> parseSent(Node Sent) {
        NodeList nodeList = Sent.getChildNodes();
        List<Rel> resList = new ArrayList<>();
        for (int k = 0; k < nodeList.getLength(); k++) {
            Node node = nodeList.item(k);
            if (node.getNodeType() != Node.TEXT_NODE && node.getNodeName().equals(RELATION)) {
                Rel rel = parseRel(node.getAttributes());
                if (rel.name == SyntaxRel.OTR_FORMA) {
                    continue;
                }
                resList.add(rel);
            }
        }
        return resList;
    }

    private static Rel parseRel(NamedNodeMap namedNodeMap) {
        Rel rel = new Rel();
        rel.name = SyntaxRel.convert(namedNodeMap.getNamedItem(NAME).getNodeValue());
        rel.grammar_child = namedNodeMap.getNamedItem(GRAMMAR_CHILD).getNodeValue();
        rel.grammar_parent = namedNodeMap.getNamedItem(GRAMMAR_PARENT).getNodeValue();
        rel.lemma_child = handlePostfix(namedNodeMap.getNamedItem(LEMMA_CHILD).getNodeValue());
        rel.lemma_parent = handlePostfix(namedNodeMap.getNamedItem(LEMMA_PARENT).getNodeValue());
        rel.start_lemma_child = rel.lemma_child;
        rel.start_lemma_parent = rel.lemma_parent;
        return rel;
    }

    private static String handlePostfix(String word) {
        if (word.endsWith("СЯ")) {
            return word.substring(0, word.length() - 2);
        }
        return word;
    }
}
