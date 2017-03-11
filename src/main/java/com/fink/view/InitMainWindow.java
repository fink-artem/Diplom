package com.fink.view;

import com.fink.ontology.OntologyCreator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import ru.nsu.cg.MainFrame;

public class InitMainWindow extends MainFrame {

    public static final String DEFAULT_TEXT = "Готово";
    private static final JLabel statusBar = new JLabel(DEFAULT_TEXT);
    private final int MIN_WIDTH = 800;
    private final int MIN_HEIGHT = 600;
    private InitView initView = new InitView();

    public InitMainWindow() throws Exception {
        super();

        File file = new File("./answer2.xml");
        OntologyCreator ontologyCreator = new OntologyCreator();
        OWLOntology owlOntology = ontologyCreator.run(file);
        IRI destination = IRI.create(new File("out.owl").toURI());
        owlOntology.getOWLOntologyManager().saveOntology(owlOntology, new OWLXMLDocumentFormat(), destination);

        setTitle("DocCheck");
        setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - MIN_WIDTH / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - MIN_HEIGHT / 2, MIN_WIDTH, MIN_HEIGHT);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        try {
            addSubMenu("Файл", KeyEvent.VK_F);
            addMenuItem("Файл/Новый", "Новый", KeyEvent.VK_X, "New.png", "onNew");
            addMenuItem("Файл/Открыть", "Открыть", KeyEvent.VK_X, "Open.png", "onOpen");
            addMenuSeparator("Файл");
            addMenuItem("Файл/Выход", "Выход", KeyEvent.VK_X, "Exit.png", "onExit");

            addSubMenu("Выполнить", KeyEvent.VK_F);
            addMenuItem("Выполнить/Запуск", "Запуск", KeyEvent.VK_X, "Run.png", "onRun");

            addSubMenu("Справка", KeyEvent.VK_H);
            addMenuItem("Справка/Об авторе", "Об авторе", KeyEvent.VK_A, "About.png", "onAbout");

            addToolBarButton("Файл/Новый", "Новый");
            addToolBarButton("Файл/Открыть", "Открыть");
            addToolBarSeparator();
            addToolBarButton("Выполнить/Запуск", "Запуск");
            addToolBarSeparator();
            addToolBarButton("Справка/Об авторе", "Об авторе");

            add(initView);

            add(statusBar, BorderLayout.SOUTH);

        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void onNew() {
        initView.clear();
    }

    public void onOpen() {
        File file = getOpenFileName("txt", "TXT");
        initView.addTextFromFile(file);
    }

    public void onRun() {
        
    }

    public static JLabel getStatusBar() {
        return statusBar;
    }

    public void onExit() {
        System.exit(0);
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(this, "Финк Артём Альбертович", "Об авторе", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) throws Exception {
        InitMainWindow mainFrame = new InitMainWindow();
        mainFrame.setVisible(true);
    }

}
