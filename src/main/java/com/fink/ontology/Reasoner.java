package com.fink.ontology;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import java.util.Set;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class Reasoner {

    private final OWLReasonerFactory reasonerFactory = new ReasonerFactory();

    public void run(OWLOntology owlOntology) {
        Configuration configuration = new Configuration();
        configuration.throwInconsistentOntologyException = false;
        OWLReasoner owlReasoner = reasonerFactory.createReasoner(owlOntology, configuration);
        Node<OWLClass> bottomNode = owlReasoner.getUnsatisfiableClasses();
        Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
        OWLClass owlClass = null;
        if (!unsatisfiable.isEmpty()) {
            System.out.println("The following classes are unsatisfiable: ");
            for (OWLClass cls : unsatisfiable) {
                System.out.println(" " + cls);
                owlClass = cls;
            }
        } else {
            System.out.println("There are no unsatisfiable classes");
        }
        BlackBoxExplanation exp = new BlackBoxExplanation(owlOntology, reasonerFactory, owlReasoner);
        HSTExplanationGenerator multExplanator = new HSTExplanationGenerator(exp);
        // Now we can get explanations for the unsatisfiability. 
        Set<Set<OWLAxiom>> explanations = multExplanator.getExplanations(owlClass);
        // Let us print them. Each explanation is one possible set of axioms that cause the 
        // unsatisfiability. 
        for (Set<OWLAxiom> explanation : explanations) {
            System.out.println("------------------");
            System.out.println("Axioms causing the unsatisfiability: ");
            for (OWLAxiom causingAxiom : explanation) {
                System.out.println(causingAxiom);
            }
            System.out.println("------------------");
        }
    }

}
