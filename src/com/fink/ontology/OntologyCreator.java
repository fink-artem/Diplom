package com.fink.ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
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
    private static final String ENCODING = "WINDOWS-1251";
    private static final String SOURCE = "http://www.semanticweb.org/admin/ontologies";
    private static final String NS = SOURCE + "#";

    class Rel {

        String name;
        String grammar_child;
        String grammar_parent;
        String lemma_child;
        String lemma_parent;
    }

    private OntModel ontModel;

    public OntModel run(File f) throws ParserConfigurationException, SAXException, IOException {
        ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource inputStore = new InputSource(new FileInputStream(f));
        inputStore.setEncoding(ENCODING);
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
        List<Rel> resList = new ArrayList<>();
        for (int k = 0; k < nodeList.getLength(); k++) {
            Node node = nodeList.item(k);
            if (node.getNodeType() != Node.TEXT_NODE && node.getNodeName().equals(RELATION)) {
                resList.add(parseRel(node.getAttributes()));
            }
        }
        int size = resList.size();
        for (int i = size - 1; i >= 0; i--) {
            Rel rel = resList.get(i);
            switch (SyntaxRel.convert(rel.name)) {
                case PODL:
                    ontModel.createClass(NS + rel.lemma_child);
                    break;
            }
        }
    }

    Rel parseRel(NamedNodeMap namedNodeMap) {
        Rel rel = new Rel();
        rel.name = namedNodeMap.getNamedItem(NAME).getNodeValue();
        rel.grammar_child = namedNodeMap.getNamedItem(GRAMMAR_CHILD).getNodeValue();
        rel.grammar_parent = namedNodeMap.getNamedItem(GRAMMAR_PARENT).getNodeValue();
        rel.lemma_child = namedNodeMap.getNamedItem(LEMMA_CHILD).getNodeValue();
        rel.lemma_parent = namedNodeMap.getNamedItem(LEMMA_PARENT).getNodeValue();
        return rel;
    }

}
