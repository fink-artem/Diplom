package com.fink.ontology;

import com.fink.logic.Link;
import com.fink.logic.Rel;
import com.fink.logic.SemanRel;
import com.fink.logic.Sent;
import com.fink.logic.SyntaxRel;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.xml.sax.SAXException;

public class OntologyCreator {

    private static final String DOCUMENT_NAME = "ДОКУМЕНТ";

    private String ns;

    private OWLDataFactory factory;
    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private static final File FRAME = new File("./frames/main.owl");

    private OWLOntology owlOntology;
    private OWLClass mainOwlClass;
    private OWLClass podlOwlClass;
    private OWLClassExpression mainClassExpression;

    public OWLOntology run(List<List<Rel>> textSynan, List<Sent> sentList) throws ParserConfigurationException, SAXException, IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        factory = manager.getOWLDataFactory();
        owlOntology = manager.loadOntologyFromOntologyDocument(FRAME);
        ns = owlOntology.getOntologyID().getOntologyIRI().get() + "#";
        mainOwlClass = getClass(DOCUMENT_NAME);
        int size = textSynan.size();
        for (int i = 0; i < size; i++) {
            parseSent(textSynan.get(i), sentList.get(i));
        }
        saveEquivalentDocument();
        //OWLDisjointClassesAxiom owlDisjointClassesAxiom = factory.getOWLDisjointClassesAxiom(owlOntology.getClassesInSignature());
        //manager.applyChange(new AddAxiom(owlOntology, owlDisjointClassesAxiom));
        return owlOntology;
    }

    private void parseSent(List<Rel> relList, Sent sent) {
        preprocessing(relList, sent);
        //printRel(relList);
        OWLObjectProperty owlObjectProperty;
        OWLIndividual owlIndividual;
        int size = relList.size();
        for (int i = size - 1; i >= 0; i--) {
            Rel rel = relList.get(i);
            switch (rel.name) {
                case PODL:
                    owlObjectProperty = getObjectProperty(rel.lemma_parent);
                    int sizeList = sent.nodeList.size();
                    int position = -1;
                    for (int j = 0; j < size; j++) {
                        if (sent.nodeList.get(j).name.equals(rel.start_lemma_child)) {
                            position = j;
                            break;
                        }
                    }
                    if (position == -1) {
                        podlOwlClass = getClass(rel.lemma_child);
                        break;
                    }
                    podlOwlClass = getClass(processingSysh(relList, sent, rel.lemma_child, position));
                    position = -1;
                    for (int j = 0; j < sizeList; j++) {
                        if (sent.nodeList.get(j).name.equals(rel.start_lemma_parent)) {
                            position = j;
                            break;
                        }
                    }
                    if (position == -1) {
                        continue;
                    }
                    for (Link link : sent.linkList) {
                        if (link.secondNodeNumber == position && link.synanType != SyntaxRel.PODL) {
                            String secondLemma = "";
                            for (Rel relList1 : relList) {
                                if (relList1.start_lemma_child.equals(sent.nodeList.get(link.firstNodeNumber).name)) {
                                    secondLemma = relList1.lemma_child;
                                    break;
                                } else if (relList1.start_lemma_parent.equals(sent.nodeList.get(link.firstNodeNumber).name)) {
                                    secondLemma = relList1.lemma_parent;
                                    break;
                                }
                            }
                            if (secondLemma.equals("")) {
                                continue;
                            }
                            owlIndividual = getIndividual(processingSysh(relList, sent, secondLemma, link.firstNodeNumber));
                            addNewEquivalent(podlOwlClass, owlObjectProperty, owlIndividual);
                        }
                    }
                    break;
                case OTSRAV:
                    owlObjectProperty = getObjectProperty(rel.lemma_parent);
                    owlIndividual = getIndividual(rel.lemma_child);
                    addEquivalent(podlOwlClass, owlObjectProperty, owlIndividual);
                    break;
            }
        }
    }

    void addNewEquivalent(OWLClass owlClassDomain, OWLObjectProperty owlObjectProperty, OWLIndividual owlIndividual) {
        if (owlObjectProperty == null) {
            return;
        }
        OWLClassExpression newExpression = factory.getOWLObjectHasValue(owlObjectProperty, owlIndividual);
        OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(owlClassDomain, newExpression);
        manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);

    }

    void addEquivalent(OWLClass owlClassDomain, OWLObjectProperty owlObjectProperty, OWLIndividual owlIndividual) {
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
            OWLClassExpression oldExpression = iterator.next();
            if (oldExpression.equals(owlClassDomain)) {
                oldExpression = iterator.next();
            }
            manager.removeAxiom(owlOntology, next);
            OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(owlClassDomain, factory.getOWLObjectIntersectionOf(oldExpression, newExpression));
            manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);
        }
    }

    void addClassEquivalent(OWLClass owlClassDomain, OWLClass owlClassRange) {
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxioms = owlOntology.getEquivalentClassesAxioms(owlClassDomain);
        if (equivalentClassesAxioms.isEmpty()) {
            OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(owlClassDomain, owlClassRange);
            manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);
        } else {
            OWLEquivalentClassesAxiom next = equivalentClassesAxioms.iterator().next();
            Iterator<OWLClassExpression> iterator = next.getClassExpressions().iterator();
            iterator.next();
            OWLClassExpression oldExpression = iterator.next();
            manager.removeAxiom(owlOntology, next);
            OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(owlClassDomain, factory.getOWLObjectIntersectionOf(oldExpression, owlClassRange));
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

    void addSubClass(OWLClass owlClass, OWLClass owlSubClass) {
        manager.applyChange(new AddAxiom(owlOntology, factory.getOWLSubClassOfAxiom(owlSubClass, owlClass)));
    }

    void copyEquivalent(OWLClass owlClass1, OWLClass owlClass2) {
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxioms = owlOntology.getEquivalentClassesAxioms(owlClass1);
        if (!equivalentClassesAxioms.isEmpty()) {
            OWLEquivalentClassesAxiom next = equivalentClassesAxioms.iterator().next();
            Iterator<OWLClassExpression> iterator = next.getClassExpressions().iterator();
            iterator.next();
            OWLClassExpression oldExpression = iterator.next();
            OWLEquivalentClassesAxiom owlEquivalentClassesAxiom = factory.getOWLEquivalentClassesAxiom(owlClass2, oldExpression);
            manager.addAxiom(owlOntology, owlEquivalentClassesAxiom);
        }
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

    void preprocessing(List<Rel> resList, Sent sent) {
        processingGlag(resList);
        processingOdnorod(resList);
        processingPril(resList);
    }

    void processingGlag(List<Rel> resList) {
        int size = resList.size();
        for (int i = 0; i < size; i++) {
            if (resList.get(i).name == SyntaxRel.OTR_FORMA) {
                String replace = "НЕ_" + resList.get(i).lemma_parent;
                for (int j = i + 1; j < size; j++) {
                    if (resList.get(j).lemma_child.equals(resList.get(i).lemma_parent)) {
                        resList.get(j).lemma_child = replace;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                for (int j = i - 1; j >= 0; j--) {
                    if (resList.get(j).lemma_child.equals(resList.get(i).lemma_parent)) {
                        resList.get(j).lemma_child = replace;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                resList.remove(i);
                i--;
                size--;
            }
            if (resList.get(i).name == SyntaxRel.PER_GLAG_INF) {
                String replace = resList.get(i).grammar_parent + "_" + resList.get(i).lemma_child;
                for (int j = i + 1; j < size; j++) {
                    if (resList.get(j).lemma_child.equals(resList.get(i).lemma_parent) || resList.get(j).lemma_child.equals(resList.get(i).lemma_child)) {
                        resList.get(j).lemma_child = replace;
                    }
                    if (resList.get(j).lemma_parent.equals(resList.get(i).lemma_parent) || resList.get(j).lemma_parent.equals(resList.get(i).lemma_child)) {
                        resList.get(j).lemma_parent = replace;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                for (int j = i - 1; j >= 0; j--) {
                    if (resList.get(j).lemma_child.equals(resList.get(i).lemma_parent) || resList.get(j).lemma_child.equals(resList.get(i).lemma_child)) {
                        resList.get(j).lemma_child = replace;
                    }
                    if (resList.get(j).lemma_parent.equals(resList.get(i).lemma_parent) || resList.get(j).lemma_parent.equals(resList.get(i).lemma_child)) {
                        resList.get(j).lemma_parent = replace;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                resList.remove(i);
                i--;
                size--;
            }
        }
    }

    void processingOdnorod(List<Rel> resList) {
        int size = resList.size();
        for (int i = 0; i < size; i++) {
            if (resList.get(i).lemma_child.equals("ИЛИ") || resList.get(i).lemma_child.equals("И") || resList.get(i).lemma_child.equals(",")) {
                for (int j = i + 1; j < size; j++) {
                    if (resList.get(j).lemma_parent.equals(resList.get(i).lemma_child)) {
                        resList.get(j).lemma_parent = resList.get(i).lemma_parent;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                for (int j = i - 1; j >= 0; j--) {
                    if (resList.get(j).lemma_parent.equals(resList.get(i).lemma_child)) {
                        resList.get(j).lemma_parent = resList.get(i).lemma_parent;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                resList.remove(i);
                i--;
                size--;
            }
        }
    }

    void processingPril(List<Rel> resList) {
        int size = resList.size();
        for (int i = 0; i < size; i++) {
            if (resList.get(i).name == SyntaxRel.PRIL_CYSCH) {
                Rel rel = resList.get(i);
                String replace = rel.lemma_child + "_" + rel.lemma_parent;
                OWLClass owlClass = getClass(replace);
                OWLIndividual owlIndividual = getIndividual(rel.lemma_child);
                OWLObjectProperty owlObjectProperty = getObjectProperty("БЫТЬ");
                if (!rel.lemma_parent.contains("_")) {
                    addClassEquivalent(owlClass, getClass(rel.lemma_parent));
                } else {
                    copyEquivalent(getClass(rel.lemma_parent), owlClass);
                }

                addEquivalent(owlClass, owlObjectProperty, owlIndividual);
                for (int j = i + 1; j < size; j++) {
                    if (resList.get(j).lemma_parent.equals(resList.get(i).lemma_parent)) {
                        resList.get(j).lemma_parent = replace;
                    }
                    if (resList.get(j).lemma_child.equals(resList.get(i).lemma_parent)) {
                        resList.get(j).lemma_child = replace;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                for (int j = i - 1; j >= 0; j--) {
                    if (resList.get(j).lemma_parent.equals(resList.get(i).lemma_parent)) {
                        resList.get(j).lemma_parent = replace;
                    }
                    if (resList.get(j).lemma_child.equals(resList.get(i).lemma_parent)) {
                        resList.get(j).lemma_child = replace;
                    }
                    if (resList.get(j).name == SyntaxRel.PODL) {
                        break;
                    }
                }
                resList.remove(i);
                i--;
                size--;
            }
        }
    }

    String processingSysh(List<Rel> relList, Sent sent, String lem, int position) {
        return dfs(position, relList, sent, lem);
    }

    String dfs(int position, List<Rel> relList, Sent sent, String lem) {
        SemanRel type;
        String lemma;
        String secondLemma = "";
        String lemma2 = null;
        for (Link link : sent.linkList) {
            if (link.secondNodeNumber == position && link.synanType != SyntaxRel.PRIL_CYSCH) {
                type = link.semanType;
                lemma = sent.nodeList.get(link.firstNodeNumber).name;
                for (Rel relList1 : relList) {
                    if (relList1.start_lemma_child.equals(lemma)) {
                        secondLemma = relList1.lemma_child;
                        break;
                    } else if (relList1.start_lemma_parent.equals(lemma)) {
                        secondLemma = relList1.lemma_parent;
                        break;
                    }
                }
                lemma2 = dfs(link.firstNodeNumber, relList, sent, secondLemma);
                convert(lem, lemma2, type);
                lem += "_" + lemma2;
            }
        }
        if (lemma2 == null) {
            return lem;
        }
        return lem;
    }

    void convert(String first, String second, SemanRel type) {
        OWLClass owlClass = getClass(first + "_" + second);
        OWLObjectProperty owlObjectProperty = null;
        OWLIndividual owlIndividual = getIndividual(second);
        switch (type) {
            case AUTHOR:
                break;
            case AGENT:
                break;
            case ADR:
                break;
            case IN_DIRECT:
                break;
            case TIME:
                break;
            case VALUE:
                break;
            case IDENT:
                break;
            case NAME:
                break;
            case INSTR:
                break;
            case SRC_PNT:
                break;
            case C_AGENT:
                break;
            case QUANTIT:
                break;
            case TRG_PNT:
                break;
            case LOK:
                owlObjectProperty = getObjectProperty("ИМЕТЬ_ЛОКАЦИЮ");
                break;
            case SCALE:
                break;
            case MATER:
                break;
            case PURP:
                break;
            case OBJ:
                break;
            case RESTR:
                break;
            case ESTIM:
                break;
            case PARAM:
                break;
            case PACIEN:
                break;
            case MEDIATOR:
                break;
            case PROPERT:
                break;
            case BELNG:
                break;
            case CAUSE:
                break;
            case RESLT:
                break;
            case CONTEN:
                break;
            case METHOD:
                break;
            case MEANS:
                break;
            case DEGREE:
                break;
            case SUB:
                owlObjectProperty = getObjectProperty("ИМЕТЬ_СУБЪЕКТ");
                break;
            case THEME:
                break;
            case AIM:
                break;
            case PART:
                break;
            case F_ACT:
                break;
            case S_ACT:
                break;
            case ACT:
                break;
            case UNDEFINED:
                break;
            default:
                throw new AssertionError(type.name());
        }
        if (owlObjectProperty != null) {
            if (!getClass(first).getIRI().getShortForm().contains("_")) {
                addClassEquivalent(owlClass, getClass(first));
            } else {
                copyEquivalent(getClass(first), owlClass);
            }
            addEquivalent(owlClass, owlObjectProperty, owlIndividual);
        }
    }

    void printRel(List<Rel> resList) {
        resList.stream().forEach((rel) -> {
            System.out.println(rel.name + " " + rel.lemma_parent + " " + rel.lemma_child);
        });
    }

}
