package com.fink.ontology;

import com.fink.logic.Rel;
import com.fink.logic.SyntaxRel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
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

    public OWLOntology run(List<List<Rel>> textSynan) throws ParserConfigurationException, SAXException, IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        factory = manager.getOWLDataFactory();
        owlOntology = manager.loadOntologyFromOntologyDocument(FRAME);
        ns = owlOntology.getOntologyID().getOntologyIRI().get() + "#";
        mainOwlClass = getClass(DOCUMENT_NAME);
        for (List<Rel> textSynan1 : textSynan) {
            parseSent(textSynan1);
        }
        saveEquivalentDocument();
        OWLDisjointClassesAxiom owlDisjointClassesAxiom = factory.getOWLDisjointClassesAxiom(owlOntology.getClassesInSignature());
        manager.applyChange(new AddAxiom(owlOntology, owlDisjointClassesAxiom));
        return owlOntology;
    }

    private void parseSent(List<Rel> resList) {
        String scas = "";
        String sysh = "";
        preprocessing(resList);
        printRel(resList);
        OWLObjectProperty owlObjectProperty;
        OWLIndividual owlIndividual;
        OWLClass owlClass;
        List<Rel> unresolvedRel = new ArrayList<>();
        int size = resList.size();
        for (int i = size - 1; i >= 0; i--) {
            Rel rel = resList.get(i);
            switch (rel.name) {
                case PODL:
                    scas = handlePostfix(rel.lemma_parent);
                    sysh = rel.lemma_child;
                    podlOwlClass = getClass(rel.lemma_child);
                    //owlIndividual = getIndividual(rel.lemma_child);
                    //owlObjectProperty = getObjectProperty(scas);

                    //addEquivalentDocument(owlObjectProperty, owlIndividual);
                    break;
                case PG:
                    owlIndividual = getIndividual(rel.lemma_child);
                    owlObjectProperty = getObjectProperty(scas + "_" + rel.lemma_parent);

                    if (podlOwlClass == null) {
                        unresolvedRel.add(rel);
                        break;
                    }
                    addEquivalent(podlOwlClass, owlObjectProperty, owlIndividual);
                    break;
                case PRYAM_DOP:
                    owlIndividual = getIndividual(rel.lemma_child);
                    owlObjectProperty = getObjectProperty(handlePostfix(rel.lemma_parent));
                    if (podlOwlClass == null) {
                        unresolvedRel.add(rel);
                        break;
                    }
                    addEquivalent(podlOwlClass, owlObjectProperty, owlIndividual);
                    break;
                case GENIT_IG:
                    owlClass = getClass(rel.lemma_parent + "_" + rel.lemma_child);
                    if (rel.lemma_parent.equals(sysh)) {
                        podlOwlClass = owlClass;
                    }
                    addSubClass(getClass(rel.lemma_parent), owlClass);
                    break;
                case OTSRAV:
                    owlObjectProperty = getObjectProperty(rel.lemma_parent);
                    owlIndividual = getIndividual(rel.lemma_child);
                    if (podlOwlClass == null) {
                        unresolvedRel.add(rel);
                        break;
                    }
                    addEquivalent(podlOwlClass, owlObjectProperty, owlIndividual);
                    break;
            }
        }
        if (podlOwlClass != null) {
            for (Rel rel : unresolvedRel) {
                owlObjectProperty = getObjectProperty(rel.lemma_parent);
                owlIndividual = getIndividual(rel.lemma_child);
                addEquivalent(podlOwlClass, owlObjectProperty, owlIndividual);
            }
        }
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

    

    void preprocessing(List<Rel> resList) {
        processingGlag(resList);
        processingOdnorod(resList);
        processingPril(resList);
        processingGenit(resList);
    }

    void processingGenit(List<Rel> resList) {
        int size = resList.size();
        for (int i = 0; i < size; i++) {
            if (resList.get(i).name == SyntaxRel.GENIT_IG) {
                String replace = resList.get(i).lemma_parent + resList.get(i).lemma_child;
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
                String replace = handlePostfix(resList.get(i).grammar_parent) + "_" + handlePostfix(resList.get(i).lemma_child);
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
                int search = rel.lemma_parent.lastIndexOf("_");
                OWLIndividual owlIndividual = getIndividual(rel.lemma_child);
                OWLObjectProperty owlObjectProperty = getObjectProperty("БЫТЬ");
                if (search == -1) {
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

    void printRel(List<Rel> resList) {
        resList.stream().forEach((rel) -> {
            System.out.println(rel.name + " " + rel.lemma_parent + " " + rel.lemma_child);
        });
    }

}
