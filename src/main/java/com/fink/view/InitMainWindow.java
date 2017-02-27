package com.fink.view;

import com.fink.ontology.OntologyCreator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.xml.sax.SAXException;
import ru.nsu.cg.MainFrame;

public class InitMainWindow extends MainFrame {

    private final int MIN_WIDTH = 800;
    private final int MIN_HEIGHT = 600;
    private InitView initView = new InitView();
    private JLabel statusBar = new JLabel("Готово");

    public InitMainWindow() throws Exception {
        super();

        File file = new File("./answer2.xml");
        OntologyCreator ontologyCreator = new OntologyCreator();
        OWLOntology owlOntology = ontologyCreator.run(file);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = folder.newFile("out.owl");
        IRI destination = IRI.create(new File("./out.owl").toURI());
        manager.saveOntology(owlOntology, new OWLXMLDocumentFormat(), destination);

        setTitle("DocCheck");
        setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - MIN_WIDTH / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - MIN_HEIGHT / 2, MIN_WIDTH, MIN_HEIGHT);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/New", "Create a new document", KeyEvent.VK_X, "New.png", "onNew", statusBar);
            addMenuItem("File/Open", "Open an existing document", KeyEvent.VK_X, "Open.png", "onOpen", statusBar);
            addMenuItem("File/Save", "Save the active document", KeyEvent.VK_X, "Save.png", "onSave", statusBar);
            addMenuSeparator("File");
            addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "Exit.png", "onExit", statusBar);

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About", "About", KeyEvent.VK_A, "About.png", "onAbout", statusBar);

            addToolBarButton("File/New", "Новый", statusBar);
            addToolBarButton("File/Open", "Открыть", statusBar);
            addToolBarButton("File/Save", "Закрыть", statusBar);
            addToolBarSeparator();
            addToolBarButton("Help/About", "Об авторе", statusBar);

            add(initView);

            add(statusBar, BorderLayout.SOUTH);

        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void onNew() {

    }

    public void onOpen() {
        File file = getOpenFileName("txt", "TXT");

    }

    public void onSave() {
        File file = getSaveFileName("owl", "ontology");
    }

    public void onExit() {
        System.exit(0);
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(this, "Финк Артём Альбертович", "Об авторе", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) throws Exception {
        InitMainWindow mainFrame = new InitMainWindow();
        //mainFrame.setVisible(true);
    }

}
