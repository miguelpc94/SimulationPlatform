package App;

import gui.SimulationGraphicsInterface;
import gui.Window;
import universes.automaticpainting.AutomaticPainting;

class Main{
    public static void main(String[] args) {
        SimulationGraphicsInterface sgi = new SimulationGraphicsInterface(1920,1000);
        AutomaticPainting automaticPainting = new AutomaticPainting();
        automaticPainting.setSimulationGraphicsInterface(sgi);
        automaticPainting.start();
        new Window(sgi);
    }
}
