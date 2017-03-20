package com.fink.ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
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
    private static final String DOCUMENT_NAME = "ДОКУМЕНТ";

    private String ns;

    class Rel {

        SyntaxRel name;
        String grammar_child;
        String grammar_parent;
        String lemma_child;
        String lemma_parent;
    }

    private OWLDataFactory factory;
    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private static final File FRAME = new File("./frames/main.owl");

    private OWLOntology owlOntology;
    private OWLClass mainOwlClass;
    private OWLClass podlOwlClass;
    private OWLClassExpression mainClassExpression;

    public OWLOntology run(File f) throws ParserConfigurationException, SAXException, IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        factory = manager.getOWLDataFactory();
        owlOntology = manager.loadOntologyFromOntologyDocument(FRAME);
        ns = owlOntology.getOntologyID().getOntologyIRI().get() + "#";
        mainOwlClass = getClass(DOCUMENT_NAME);
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
                Rel rel = parseRel(node.getAttributes());
                if (rel.name == SyntaxRel.OTR_FORMA) {

                    continue;
                }
                resList.add(rel);
            }
        }
        String scas = "";
        int size = resList.size();
        OWLObjectProperty owlObjectProperty;
        OWLIndividual owlIndividual;
        OWLClass owlClass;
        for (int i = size - 1; i >= 0; i--) {
            Rel rel = resList.get(i);
            switch (rel.name) {
                case PODL:
                    scas = handlePostfix(rel.lemma_parent);
                    podlOwlClass = getClass(rel.lemma_child);
                    owlIndividual = getIndividual(rel.lemma_child);
                    owlObjectProperty = getObjectProperty(scas);

                    addEquivalentDocument(owlObjectProperty, owlIndividual);
                    break;
                case PG:
                    owlIndividual = getIndividual(rel.lemma_child);
                    owlObjectProperty = getObjectProperty(scas + "_" + rel.lemma_parent);

                    addEquivalent(owlObjectProperty, podlOwlClass, owlIndividual);
                    break;
                case PRYAM_DOP:
                    owlIndividual = getIndividual(rel.lemma_child);
                    owlObjectProperty = getObjectProperty(handlePostfix(rel.lemma_parent));

                    addEquivalent(owlObjectProperty, podlOwlClass, owlIndividual);
                    break;
                case PRIL_CYSCH:
                    owlClass = getClass(rel.lemma_parent);
                    owlIndividual = getIndividual(rel.lemma_child);
                    owlObjectProperty = getObjectProperty("БЫТЬ");

                    addEquivalent(owlObjectProperty, owlClass, owlIndividual);
                    break;
                case GENIT_IG:
                    owlClass = getClass(rel.lemma_parent);
                    owlIndividual = getIndividual(rel.lemma_child);
                    owlObjectProperty = getObjectProperty("ИМЕТЬ");

                    addEquivalent(owlObjectProperty, owlClass, owlIndividual);
                    break;
                case PER_GLAG_INF:
                    scas = handlePostfix(rel.grammar_parent) + "_" + handlePostfix(rel.lemma_child);
                    getObjectProperty(scas);

                    break;
                case OTR_FORMA:
                    break;
            }
        }
    }

    void addEquivalent(OWLObjectProperty owlObjectProperty, OWLClass owlClassDomain, OWLIndividual owlIndividual) {
        if (owlObjectProperty == null) {
            return;
        }
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxioms = owlOntology.getEquivalentClassesAxioms(owlClassDomain);
        OWLClassExpression newExpression = factory.getOWLObjectHasValue(owlObjectProperty, owlIndividual);
        if (equivalentClassesAxioms.isEmpty()) {
            OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(owlClassDomain, newExpression);
            manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);
        } else {
            OWLEquivalentClassesAxiom next = equivalentClassesAxioms.iterator().next();
            Iterator<OWLClassExpression> iterator = next.getClassExpressions().iterator();
            iterator.next();
            OWLClassExpression oldExpression = iterator.next();
            manager.removeAxiom(owlOntology, next);
            OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(owlClassDomain, factory.getOWLObjectIntersectionOf(oldExpression, newExpression));
            manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);
        }
    }

    void addEquivalentDocument(OWLObjectProperty owlObjectProperty, OWLIndividual owlIndividual) {
        if (owlObjectProperty == null) {
            return;
        }
        OWLClassExpression newExpression = factory.getOWLObjectHasValue(owlObjectProperty, owlIndividual);
        if (mainClassExpression == null) {
            mainClassExpression = newExpression;
        } else {
            mainClassExpression = factory.getOWLObjectIntersectionOf(mainClassExpression, newExpression);
        }
    }

    String handlePostfix(String word) {
        if (word.endsWith("СЯ")) {
            return word.substring(0, word.length() - 2);
        }
        return word;
    }

    OWLClass getClass(String name) {
        return factory.getOWLClass(IRI.create(ns + name));
    }

    OWLIndividual getIndividual(String name) {
        OWLIndividual owlIndividual = factory.getOWLNamedIndividual(IRI.create(ns + name));
        OWLClass owlClass = factory.getOWLClass(IRI.create(ns + name));
        OWLClassAssertionAxiom owlClassAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, owlIndividual);
        manager.addAxiom(owlOntology, owlClassAssertionAxiom);
        return owlIndividual;
    }

    OWLObjectProperty getObjectProperty(String name) {
        return factory.getOWLObjectProperty(IRI.create(ns + name));
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
        rel.name = SyntaxRel.convert(namedNodeMap.getNamedItem(NAME).getNodeValue());
        rel.grammar_child = namedNodeMap.getNamedItem(GRAMMAR_CHILD).getNodeValue();
        rel.grammar_parent = namedNodeMap.getNamedItem(GRAMMAR_PARENT).getNodeValue();
        rel.lemma_child = namedNodeMap.getNamedItem(LEMMA_CHILD).getNodeValue();
        rel.lemma_parent = namedNodeMap.getNamedItem(LEMMA_PARENT).getNodeValue();
        return rel;
    }

}
