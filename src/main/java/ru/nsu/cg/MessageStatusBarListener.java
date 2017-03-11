package ru.nsu.cg;

import com.fink.view.InitMainWindow;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class MessageStatusBarListener implements MouseInputListener {

    private final String text;

    public MessageStatusBarListener(String text) {
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
        InitMainWindow.getStatusBar().setText(InitMainWindow.DEFAULT_TEXT);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        InitMainWindow.getStatusBar().setText(text);
    }

}
