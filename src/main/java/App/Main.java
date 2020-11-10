package App;

import gui.SimulationGraphicsInterface;
import gui.Window;
import universes.automaticpainting.AutomaticPainting;
import universes.automaticpainting.ExperimentInterface;

import java.util.ArrayList;
import java.util.List;

class Main{

    static int NUMBER_OF_UNIVERSES = 1;
    static int NUMBER_OF_WORKERS = 4;
    static int POPULATION_SIZE = 20000;

    public static void main(String[] args) {

        SimulationGraphicsInterface sgi = new SimulationGraphicsInterface(1920,1000);


        List<AutomaticPainting> automaticPaintingList = new ArrayList<>();
        for (int universeNumber=0; universeNumber<NUMBER_OF_UNIVERSES; universeNumber++) {
            ExperimentInterface experimentInterface = new ExperimentInterface();
            AutomaticPainting automaticPainting = new AutomaticPainting();
            automaticPainting.setUniverseName("unw"+NUMBER_OF_WORKERS+"p"+POPULATION_SIZE);
            automaticPainting.setNumberOfWorkers(NUMBER_OF_WORKERS);
            automaticPainting.setPopulationSize(POPULATION_SIZE);
            automaticPainting.setSimulationGraphicsInterface(sgi);
            automaticPainting.setSeed(42);
            automaticPainting.setExperimentInterface(new ExperimentInterface());
            automaticPainting.start();
        }

        new Window(sgi);

    }
}
