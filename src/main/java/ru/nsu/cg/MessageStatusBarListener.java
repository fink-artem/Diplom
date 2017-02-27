package ru.nsu.cg;

import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

public class MessageStatusBarListener implements MouseInputListener {

    private JLabel statusBar;
    private String text;

    public MessageStatusBarListener(JLabel statusBar, String text) {
        this.statusBar = statusBar;
        this.text = text;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
        statusBar.setText("Ready");
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        statusBar.setText(text);
    }

}
