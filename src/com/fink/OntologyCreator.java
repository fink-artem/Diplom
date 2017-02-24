package com.fink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OntologyCreator {

    private static final String SENTENCE = "sent";
    private static final String RELATION = "rel";
    private static final String NAME = "name";
    private static final String GRAMMAR_CHILD = "grmchld";
    private static final String GRAMMAR_PARENT = "grmprnt";
    private static final String LEMMA_CHILD = "lemmchld";
    private static final String LEMMA_PARENT = "lemmprnt";
    
    private static final OntModel ontModel = ModelFactory.createOntologyModel();

    OntModel run(File f) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputStore = new InputSource(new FileInputStream(f));
        inputStore.setEncoding("WINDOWS-1251");
        Document document = documentBuilder.parse(inputStore);
        Node root = document.getDocumentElement();
        NodeList nodeSecondLevelList = root.getChildNodes();
        for (int i = 0; i < nodeSecondLevelList.getLength(); i++) {
            Node nodeSecondLevel = nodeSecondLevelList.item(i);
            if (nodeSecondLevel.getNodeType() != Node.TEXT_NODE && nodeSecondLevel.getNodeName().equals(SENTENCE)) {
                parseSent(nodeSecondLevel);
            }
        }
        return ontModel;
    }

    private void parseSent(Node Sent) {
        NodeList nodeList = Sent.getChildNodes();
        for (int k = 0; k < nodeList.getLength(); k++) {
            Node node = nodeList.item(k);
            if (node.getNodeType() != Node.TEXT_NODE && node.getNodeName().equals(RELATION)) {
                parseRel(node.getAttributes());
                return;
            }
        }
    }

    void parseRel(NamedNodeMap namedNodeMap) {
        System.out.println(namedNodeMap.getNamedItem(NAME).getNodeValue());
        switch(SyntaxRel.convert(namedNodeMap.getNamedItem(NAME).getNodeValue())){
            case PODL:
                
                return;
        }
    }

}
