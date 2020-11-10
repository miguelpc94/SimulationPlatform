package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyHandler implements KeyListener {
    private volatile Map<Character,Boolean> isPressed;

    public KeyHandler(SimulationPanel panel) {
        isPressed = new HashMap<>();
        panel.addKeyListener(this);
    }

    public boolean isKeyCharPressed(char key) {
        return isPressed.getOrDefault(key,false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        isPressed.put(e.getKeyChar(),true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        isPressed.put(e.getKeyChar(),false);
    }
}
