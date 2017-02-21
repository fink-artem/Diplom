
package com.fink;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        //SyntaxParser.parse(new File("./answer.txt"));
        File f = new File("./answer.txt");
        OntologyCreator.run(f);
    }
    
}
