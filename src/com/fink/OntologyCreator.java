package com.fink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OntologyCreator {

    private static final String SENT = "sent";
    private static final String REL = "rel";

    class Token {

        String word;
        String lemma;
        PartOfSpeech part_of_speech;
    }

    static void run(File f) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputStore = new InputSource(new FileInputStream(f));
        inputStore.setEncoding("WINDOWS-1251");
        Document document = documentBuilder.parse(inputStore);
        Node root = document.getDocumentElement();
        NodeList nodeSecondLevelList = root.getChildNodes();
        for (int i = 0; i < nodeSecondLevelList.getLength(); i++) {
            Node nodeSecondLevel = nodeSecondLevelList.item(i);
            if (nodeSecondLevel.getNodeType() != Node.TEXT_NODE && nodeSecondLevel.getNodeName().equals(SENT)) {
                parseSent(nodeSecondLevel);
                return;
            }
        }
    }

    static private void parseSent(Node Sent) {
        NodeList nodeList = Sent.getChildNodes();
        for (int k = 0; k < nodeList.getLength(); k++) {
            Node node = nodeList.item(k);
            if (node.getNodeType() != Node.TEXT_NODE && node.getNodeName().equals(REL)) {
                NamedNodeMap namedNodeMap = node.getAttributes();
                System.out.println(namedNodeMap.item(0).getNodeValue()); 
                System.out.println(namedNodeMap.item(1));
                System.out.println(namedNodeMap.item(2));
                System.out.println(namedNodeMap.item(3));
                System.out.println(namedNodeMap.item(4));
                System.out.println(namedNodeMap.item(5));
                return;
            }
        }
    }

    
}
