package com.fink.view;

import com.fink.ontology.Constant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class InitView extends JPanel {

    private final JTextArea leftTextArea = new JTextArea();
    private final JTextArea rightTextArea = new JTextArea();

    public InitView() {
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

    void addTextFromFile(File file) {
        JTextArea textArea;
        if (leftTextArea.getText().equals("")) {
            textArea = leftTextArea;
        } else {
            textArea = rightTextArea;
        }
        byte[] buffer = new byte[Constant.BUFFER_SIZE];
        int len;
        try (InputStream in = new FileInputStream(file)) {
            for (;;) {
                len = in.read(buffer, 0, Constant.BUFFER_SIZE);
                if (len == -1) {
                    break;
                }
                textArea.append(new String(buffer, 0, len));
            }
        } catch (IOException | NullPointerException ex) {
        }
    }

    void clear() {
        leftTextArea.setText("");
        rightTextArea.setText("");
    }

}
