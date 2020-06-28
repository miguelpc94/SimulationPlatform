package universes.automaticpainting;

import gui.GraphicInstructions;
import gui.SimulationGraphicsInterface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


// TODO: Work on this universe <======== 6 (tail)

public class AutomaticPainting extends Thread {

    private boolean running = false;

    private Random rand;
    private int seed;
    private int smokeCounter;
    private final int SMOKE_COUNTER_TRIGGER = 1;



    private final int BRUSH_POPULATION  = 20000;
    private final int WORKERS = 2;


    List<UniversePaintingWorker> workers;
    List<WorkerStatus> workerStatus;

    private String universeName;

    SimulationGraphicsInterface sgi;

    final boolean LIMIT_UPS = false;
    final double UPDATE_HERTZ = 50000;
    final double TBU = 1000000000/UPDATE_HERTZ; // Time Before Render

    List<List<AutomaticBrush>> automaticBrushes;

    public void setUniverseName(String universeName) {
        this.universeName = universeName;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public void setSimulationGraphicsInterface(SimulationGraphicsInterface sgi) {
        this.sgi = sgi;
    }



    public void run() {


        rand = new Random(seed);
        automaticBrushes = new ArrayList<>();

        int brushPopPerWorker = BRUSH_POPULATION/WORKERS;
        workers = new ArrayList<>();
        workerStatus = new ArrayList<>();
        for (int i=0; i<WORKERS; i++) {
            UniversePaintingWorker worker = new UniversePaintingWorker();
            WorkerStatus status = new WorkerStatus();
            worker.setStatus(status);
            worker.setSeed(rand.nextInt());
            worker.setWorkerName(universeName + " w" + i);
            worker.setSimulationGraphicsInterface(sgi);;
            worker.setPopulationSize(brushPopPerWorker);
            worker.start();
            workers.add(worker);
            workerStatus.add(status);
        }

        setName(universeName+"main");

        running = true;
        System.out.println( universeName + " running...");


        double lastUpdateTime = System.nanoTime();
        double lastUPSReportTime = System.currentTimeMillis();
        double startTime = System.currentTimeMillis();
        long updateCounter = 0;
        long nextMinuteMark = 60000;

        while(running) {
            double now = System.nanoTime();
            if (LIMIT_UPS) {
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
            }
            try {
                update();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //lastUpdateTime = System.nanoTime();
            updateCounter++;

            double timeSinceLastUPSReport = System.currentTimeMillis() - lastUPSReportTime;
            if (timeSinceLastUPSReport >= 5000) {
                double ups = updateCounter / ((System.currentTimeMillis()-startTime)/1000);
                System.out.println(ups);
                if ((System.currentTimeMillis()-startTime)>=nextMinuteMark) {
                    System.out.println((nextMinuteMark/60000)+" minute mark / Counter: "+updateCounter);
                    nextMinuteMark += 60000;
                }
                //updateCounter = 0;
                lastUPSReportTime = System.currentTimeMillis();
            }

        }
    }

    // This is where the universe update loop happens
    private void update() throws ExecutionException, InterruptedException {

        List<GraphicInstructions> instructionList = new LinkedList<>();
        GraphicInstructions environmentInstructions = new GraphicInstructions();

        smokeCounter++;
        if (smokeCounter>SMOKE_COUNTER_TRIGGER) {
            environmentInstructions.addBox(0,0, sgi.getWidth(), sgi.getHeight(),10,0,0,0,true);
            smokeCounter = 0;
        }

        instructionList.add(environmentInstructions);
        sgi.setInstructionList(universeName, instructionList);

        for (int i=0; i< WORKERS; i++) {
            workerStatus.get(i).work();
        }

        boolean workersBusy = true;
        while(workersBusy) {
            workersBusy = false;
            for (WorkerStatus status : workerStatus) {
                workersBusy = !status.isDone();
            }
        }

        //System.out.println("Workers done");


        /*
        List<Future<GraphicInstructions>> futureGraphicInstructions = new LinkedList<>();
        for (List<AutomaticBrush> popSeg : automaticBrushes) {
            futureGraphicInstructions.add(executorService.submit(() -> {
                final List<AutomaticBrush> population = popSeg;
                GraphicInstructions instructions = new GraphicInstructions();
                for (AutomaticBrush automaticBrush : population) {
                    automaticBrush.draw(instructions);
                }
                return instructions;
            }));
        }

        while(futureGraphicInstructions.size()>0) {
            for (int i = 0; i<futureGraphicInstructions.size(); i++) {
                Future<GraphicInstructions> futureGraphicInstruction = futureGraphicInstructions.get(i);
                if (futureGraphicInstruction.isDone()) {
                    instructionList.add(futureGraphicInstruction.get());
                    futureGraphicInstructions.remove(i);
                    i--;
                }
            }
        }
        */


    }

    public void setRunning(boolean running) {
        running = running;
    }
}
