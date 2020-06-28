package App;

import gui.SimulationGraphicsInterface;
import gui.Window;
import universes.automaticpainting.AutomaticPainting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Main{

    static int NUMBER_OF_UNIVERSES = 1;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SimulationGraphicsInterface sgi = new SimulationGraphicsInterface(1920,1000);


        List<AutomaticPainting> automaticPaintingList = new ArrayList<>();
        for (int universeNumber=0; universeNumber<NUMBER_OF_UNIVERSES; universeNumber++) {
            AutomaticPainting automaticPainting = new AutomaticPainting();
            automaticPainting.setUniverseName("un"+universeNumber);
            automaticPainting.setSimulationGraphicsInterface(sgi);
            automaticPainting.setSeed(42 + universeNumber);
            automaticPainting.start();
            automaticPaintingList.add(automaticPainting);
        }

        new Window(sgi);

    }
}
