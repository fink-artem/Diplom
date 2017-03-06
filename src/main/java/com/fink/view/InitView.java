package com.fink.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class InitView extends JPanel {

    private JTextArea leftTextArea = new JTextArea();
    private JTextArea rightTextArea = new JTextArea();

    public InitView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftTextArea, rightTextArea);
        splitPane.setResizeWeight(0.5);
        splitPane.setEnabled(false);
        add(splitPane);
    }

}
