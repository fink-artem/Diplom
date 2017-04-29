package com.fink.ontology;

import java.util.Set;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class Reasoner {

    private final OWLReasonerFactory reasonerFactory = new ReasonerFactory();
    private OWLDataFactory factory;

    public void run(OWLOntology owlOntology) {
        factory = owlOntology.getOWLOntologyManager().getOWLDataFactory();
        Configuration configuration = new Configuration();
        configuration.throwInconsistentOntologyException = false;
        OWLReasoner owlReasoner = reasonerFactory.createReasoner(owlOntology, configuration);
        Node<OWLClass> bottomNode = owlReasoner.getUnsatisfiableClasses();
        Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
        OWLClass owlClass = null;
        if (!unsatisfiable.isEmpty()) {
            for (OWLClass cls : unsatisfiable) {
                System.out.println(" " + cls);
                owlClass = cls;
            }
        } 
        
        ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createLaconicExplanationGeneratorFactory(reasonerFactory);

        ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(owlOntology);

        OWLAxiom entailment = factory.getOWLEquivalentClassesAxiom(owlClass, factory.getOWLNothing());
        
        Set<Explanation<OWLAxiom>> expl = gen.getExplanations(entailment);
        expl.stream().forEach((expl1) -> System.out.println(expl1));

    }

}
