package com.fink.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class View extends JPanel {

    private final JTextArea leftTextArea = new JTextArea();
    private final JTextArea rightTextArea = new JTextArea();

    public View() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        leftTextArea.setFont(new Font(TOOL_TIP_TEXT_KEY, Font.BOLD, 12));
        JScrollPane leftscrollPane = new JScrollPane(leftTextArea);
        rightTextArea.setFont(new Font(TOOL_TIP_TEXT_KEY, Font.BOLD, 12));
        JScrollPane rightscrollPane = new JScrollPane(rightTextArea);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftscrollPane, rightscrollPane);
        splitPane.setResizeWeight(0.5);
        splitPane.setEnabled(false);
        add(splitPane);
    }

    void addText(String text) {
        JTextArea textArea;
        if (leftTextArea.getText().equals("")) {
            textArea = leftTextArea;
        } else {
            textArea = rightTextArea;
        }
        textArea.setText(text);
    }

    void clear() {
        leftTextArea.setText("");
        rightTextArea.setText("");
    }

    boolean isEmpty() {
        return leftTextArea.getText().equals("") && rightTextArea.getText().equals("");
    }

    String getLeftText() {
        return leftTextArea.getText();
    }
    
    String getRightText() {
        return rightTextArea.getText();
    }

}
