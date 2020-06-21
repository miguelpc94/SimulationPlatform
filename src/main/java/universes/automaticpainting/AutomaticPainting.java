package universes.automaticpainting;

import gui.GraphicInstructions;
import gui.SimulationGraphicsInterface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


// TODO: Work on this universe <======== 6 (tail)

public class AutomaticPainting extends Thread {

    private boolean running = false;

    private Random rand;
    private int seed;
    private int smokeCounter;
    private final int SMOKE_COUNTER_TRIGGER = 6;

    private final int BRUSH_POPULATION  = 1200;

    private final int NUMBER_OF_WORKERS = 5;

    SimulationGraphicsInterface sgi;

    final double UPDATE_HERTZ = 500;
    final double TBU = 1000000000/UPDATE_HERTZ; // Time Before Render

    List<List<AutomaticBrush>> automaticBrushes;

    public void setSimulationGraphicsInterface(SimulationGraphicsInterface sgi) {
        this.sgi = sgi;
    }

    private List<AutomaticBrush> populateAutomaticBrushes(int n) {
        List<AutomaticBrush> newAutomaticBrushes = new ArrayList<>();
        for (int i=0; i<n; i++) {
            int width = sgi.getWidth();
            int height = sgi.getHeight();
            int brushX = (int)(rand.nextDouble()*width);
            int brushY = (int)(rand.nextDouble()*height);
            AutomaticBrush automaticBrush = new AutomaticBrush(brushX,brushY,rand,width,height);
            newAutomaticBrushes.add(automaticBrush);
        }
        return newAutomaticBrushes;
    }

    List<UniversePaintingWorker> workers;
    List<WorkerStatus> workerStatus;

    public void run() {

        seed = 11;
        rand = new Random(seed);
        automaticBrushes = new ArrayList<>();
        for (int i=0; i<NUMBER_OF_WORKERS; i++) automaticBrushes.add(populateAutomaticBrushes(BRUSH_POPULATION));

        setName("universe-main");

        running = true;
        System.out.println("Universe running...");

        workerStatus = new ArrayList<>();
        workers = new ArrayList<>();
        for (int i=0; i<NUMBER_OF_WORKERS; i++) {
            WorkerStatus status = new WorkerStatus();
            UniversePaintingWorker worker = new UniversePaintingWorker();
            worker.setStatus(status);
            worker.start();
            workerStatus.add(status);
            workers.add(worker);
        }

        double lastUpdateTime = System.nanoTime();
        double lastUPSReportTime = System.currentTimeMillis();
        int updateCounter = 0;

        while(running) {
            double now = System.nanoTime();
            while ((now-lastUpdateTime) < TBU) {
                long millisToSleep = (long)((TBU - (now-lastUpdateTime))/1000000);
                if (millisToSleep>0) {
                    try {
                        yield();
                        sleep(millisToSleep/2);
                    } catch (Exception e) {
                        System.out.println("Error yielding thread");
                    }
                }
                now = System.nanoTime();
            }
            lastUpdateTime = System.nanoTime();
            update();
            updateCounter++;

            double timeSinceLastUPSReport = System.currentTimeMillis() - lastUPSReportTime;
            if (timeSinceLastUPSReport >= 5000) {
                double ups = updateCounter / (timeSinceLastUPSReport/1000);
                System.out.println(ups);
                updateCounter = 0;
                lastUPSReportTime = System.currentTimeMillis();
            }

        }
    }




    // This is where the universe update loop happens
    private void update() {

        List<GraphicInstructions> instructionList = new LinkedList<>();
        GraphicInstructions environmentInstructions = new GraphicInstructions();

        smokeCounter++;
        if (smokeCounter>SMOKE_COUNTER_TRIGGER) {
            environmentInstructions.addBox(0,0, sgi.getWidth(), sgi.getHeight(),10,255,255,255,true);
            smokeCounter = 0;
        }

        instructionList.add(environmentInstructions);

        for (int i=0; i<NUMBER_OF_WORKERS; i++) {
            workerStatus.get(i).work(automaticBrushes.get(i));
        }

        int workersBusy = NUMBER_OF_WORKERS;
        while(workersBusy > 0) {
            workersBusy = NUMBER_OF_WORKERS;
            for (WorkerStatus status : workerStatus) {
                if (status.isDone()) workersBusy--;
            }
        };

        for (int i=0; i<NUMBER_OF_WORKERS; i++) instructionList.add(workerStatus.get(i).getInstructions());

        sgi.setInstructionList(instructionList);
    }

    public void setRunning(boolean running) {
        running = running;
    }
}
