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
    private final int SMOKE_COUNTER_TRIGGER = 0;


    private int populationSize = 20000;
    private int numberOfWorkers = 4;


    List<UniversePaintingWorker> workers;
    List<WorkerStatus> workerStatus;

    private String universeName;

    SimulationGraphicsInterface sgi;
    ExperimentInterface experimentInterface;

    private boolean limitUPS = false;
    private double updateHertz = 60;
    private double tbu = 1000000000/ updateHertz; // Time Before Render

    List<List<AutomaticBrush>> automaticBrushes;

    public void setUniverseName(String universeName) {
        this.universeName = universeName;
    }

    public void setUPSLimit(double hertz) {
        limitUPS = true;
        updateHertz = hertz;
        tbu = 1000000000/ updateHertz; // Time Before Render
    }

    public void setPopulationSize(int populationSize) {this.populationSize = populationSize;}

    public void setNumberOfWorkers(int numberOfWorkers) {this.numberOfWorkers = numberOfWorkers;}

    public void setSeed(int seed) { this.seed = seed; }

    public void setExperimentInterface(ExperimentInterface experimentInterface) { this.experimentInterface = experimentInterface; }

    public void setSimulationGraphicsInterface(SimulationGraphicsInterface sgi) { this.sgi = sgi; }

    private void letThereBeLight() {
        setName(universeName+"main");

        rand = new Random(seed);
        automaticBrushes = new ArrayList<>();

        int brushPopPerWorker = populationSize / numberOfWorkers;
        workers = new ArrayList<>();
        workerStatus = new ArrayList<>();
        for (int i = 0; i< numberOfWorkers; i++) {
            UniversePaintingWorker worker = new UniversePaintingWorker();
            WorkerStatus status = new WorkerStatus();
            worker.setStatus(status);
            worker.setSeed(rand.nextInt());
            worker.setWorkerName(universeName + " w" + i);
            worker.setSimulationGraphicsInterface(sgi);
            worker.setPopulationSize(brushPopPerWorker);
            worker.start();
            workers.add(worker);
            workerStatus.add(status);
        }

        running = true;
        System.out.println( universeName + " running...");
    }

    public void run() {

        this.letThereBeLight();

        double lastUpdateTime = System.nanoTime();
        double lastUPSReportTime = System.currentTimeMillis();
        double startTime = System.currentTimeMillis();
        long updateCounter = 0;
        long nextMinuteMark = 60000;

        while(running) {
            double now = System.nanoTime();
            if (limitUPS) {
                while ((now-lastUpdateTime) < tbu) {
                    long millisToSleep = (long)((tbu - (now-lastUpdateTime))/1000000);
                    if (millisToSleep>0) {
                        try {
                            yield();
                            sleep(millisToSleep/2);
                        } catch (Exception e) {
                            System.out.println(universeName + ": Error yielding thread");
                        }
                    }
                    now = System.nanoTime();
                }
            }

            lastUpdateTime = System.nanoTime();
            updateCounter++;
            userInput();
            update();

            double timeSinceLastUPSReport = System.currentTimeMillis() - lastUPSReportTime;
            if (timeSinceLastUPSReport >= 1000) {
                lastUPSReportTime = System.currentTimeMillis();
                double ups = updateCounter / (timeSinceLastUPSReport/1000);
                updateCounter = 0;
                if (experimentInterface != null) experimentInterface.logUPS(ups);
                /*
                if ((System.currentTimeMillis()-startTime)>=nextMinuteMark) {
                    System.out.println((nextMinuteMark/60000)+" minute mark / Counter: "+updateCounter);
                    nextMinuteMark += 60000;
                }
                */
            }

        }
        System.out.println(universeName + ": the end has come");
        dismissAllWorkers();
    }

    private void dismissAllWorkers() {
        for (UniversePaintingWorker worker : workers) {
            worker.dismiss();
        }
    }

    private void userInput() {
        //if (sgi.isKeyCharPressed('a')) System.out.println("Aaaaaaahhh!!");
    }

    private void update() {

        List<GraphicInstructions> instructionList = new LinkedList<>();
        GraphicInstructions environmentInstructions = new GraphicInstructions();

        smokeCounter++;
        if (smokeCounter>SMOKE_COUNTER_TRIGGER) {
            environmentInstructions.addBox(0,0, sgi.getWidth(), sgi.getHeight(),10,0,0,0,true);
            smokeCounter = 0;
        }

        instructionList.add(environmentInstructions);
        sgi.setInstructionList(universeName, instructionList);

        for (int i = 0; i< numberOfWorkers; i++) {
            workerStatus.get(i).work();
        }

        boolean workersBusy = true;
        while(workersBusy) {
            workersBusy = false;
            for (WorkerStatus status : workerStatus) {
                workersBusy = !status.isDone();
            }
        }

    }

    public void apocalipse() {
        running = false;
    }
}
