package com.fink.ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
    private static final String BASE = "http://www.semanticweb.org/admin/ontologies";
    private static final String NS = BASE + "#";
    private static final String DOCUMENT_NAME = "ДОКУМЕНТ";

    class Rel {

        String name;
        String grammar_child;
        String grammar_parent;
        String lemma_child;
        String lemma_parent;
    }

    private OWLDataFactory factory;
    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private OWLOntology owlOntology;
    private OWLClass mainOwlClass;
    private OWLClass podlOwlClass;
    private OWLClassExpression podlClassExpression;
    private OWLClassExpression mainClassExpression;

    public OWLOntology run(File f) throws ParserConfigurationException, SAXException, IOException, OWLOntologyCreationException {
        factory = manager.getOWLDataFactory();
        owlOntology = manager.createOntology(IRI.create(BASE));
        mainOwlClass = addClass(DOCUMENT_NAME);
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
                saveEquivalent();
            }
        }
        saveEquivalentDocument();
        OWLDisjointClassesAxiom owlDisjointClassesAxiom = factory.getOWLDisjointClassesAxiom(owlOntology.getClassesInSignature());
        manager.applyChange(new AddAxiom(owlOntology, owlDisjointClassesAxiom));
        return owlOntology;
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
        String scas = "";
        int size = resList.size();
        OWLObjectProperty owlObjectProperty;
        OWLClass owlClass;
        for (int i = size - 1; i >= 0; i--) {
            Rel rel = resList.get(i);
            switch (SyntaxRel.convert(rel.name)) {
                case PODL:
                    saveEquivalent();
                    scas = rel.lemma_parent;
                    owlClass = addClass(rel.lemma_child);
                    podlOwlClass = owlClass;
                    owlObjectProperty = factory.getOWLObjectProperty(IRI.create(NS + rel.lemma_parent));
                    //OWLFunctionalObjectPropertyAxiom owlfdpa = factory.getOWLFunctionalObjectPropertyAxiom(owlObjectProperty);
                    //manager.applyChange(new AddAxiom(owlOntology, owlObjectProperty));                    

                    addEquivalentDocument(owlObjectProperty, owlClass);
                    break;
                case PG:
                    owlClass = addClass(rel.lemma_child);

                    owlObjectProperty = factory.getOWLObjectProperty(IRI.create(NS + scas + "_" + rel.lemma_parent));

                    addEquivalent(owlObjectProperty, owlClass);
                    break;
                case PRYAM_DOP:
                    owlClass = addClass(rel.lemma_child);
                    owlObjectProperty = factory.getOWLObjectProperty(IRI.create(NS + rel.lemma_parent));

                    addEquivalent(owlObjectProperty, owlClass);
                    break;
            }
        }
    }

    void addEquivalent(OWLObjectProperty owlObjectProperty, OWLClass owlClass) {
        if (owlObjectProperty == null) {
            return;
        }
        OWLClassExpression newExpression = factory.getOWLObjectSomeValuesFrom(owlObjectProperty, owlClass);
        if (podlClassExpression == null) {
            podlClassExpression = newExpression;
        } else {
            podlClassExpression = factory.getOWLObjectIntersectionOf(podlClassExpression, newExpression);
        }

    }

    void addEquivalentDocument(OWLObjectProperty owlObjectProperty, OWLClass owlClass) {
        if (owlObjectProperty == null) {
            return;
        }
        OWLClassExpression newExpression = factory.getOWLObjectSomeValuesFrom(owlObjectProperty, owlClass);
        if (mainClassExpression == null) {
            mainClassExpression = newExpression;
        } else {
            mainClassExpression = factory.getOWLObjectIntersectionOf(mainClassExpression, newExpression);
        }
    }

    OWLClass addClass(String name) {
        OWLClass owlClass = factory.getOWLClass(IRI.create(NS + name));
        manager.addAxiom(owlOntology, factory.getOWLDeclarationAxiom(owlClass));
        return owlClass;
    }

    void saveEquivalent() {
        if (podlClassExpression == null) {
            return;
        }
        OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(podlOwlClass, podlClassExpression);
        manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);
        podlClassExpression = null;
    }

    void saveEquivalentDocument() {
        if (mainClassExpression == null) {
            return;
        }
        OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(mainOwlClass, mainClassExpression);
        manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);
        mainClassExpression = null;
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
