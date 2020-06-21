package gui;

import javax.swing.JFrame;

public class Window extends JFrame {

    public Window(SimulationGraphicsInterface sgi) {
        setTitle("Simulation Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setContentPane(new SimulationPanel(sgi));
        pack();
        setLocationRelativeTo(null);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setResizable(false);
    }
}
