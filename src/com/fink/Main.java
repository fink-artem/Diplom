
package com.fink;

import java.io.File;
import org.apache.jena.ontology.OntModel;

public class Main {

    public static void main(String[] args) throws Exception {
        //SyntaxParser.parse(new File("./answer.txt"));
        File f = new File("./answer.txt");
        OntologyCreator ontologyCreator = new OntologyCreator();
        OntModel ontModel = ontologyCreator.run(f);
    }
    
}
