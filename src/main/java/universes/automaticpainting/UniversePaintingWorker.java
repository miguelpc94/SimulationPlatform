package universes.automaticpainting;

import gui.GraphicInstructions;
import gui.SimulationGraphicsInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UniversePaintingWorker extends Thread {
    private Random rand;
    private int seed;
    SimulationGraphicsInterface sgi;
    private String workerName;
    private WorkerStatus status;
    List<AutomaticBrush> automaticBrushes;
    private int populationSize;
    private boolean running = false;


    public void setStatus(WorkerStatus status) {
        this.status = status;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }

    public void setSeed(int seed) {
        this.seed = seed;
    }

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

    public void run() {
        rand = new Random(seed);
        this.automaticBrushes = populateAutomaticBrushes(populationSize);
        running = true;
        System.out.println(workerName + " running...");
        while (running) {
            if (!status.isDone()) {

                for (AutomaticBrush automaticBrush : automaticBrushes) {
                    automaticBrush.draw(new GraphicInstructions());
                }
                GraphicInstructions instructions = new GraphicInstructions();
                for (AutomaticBrush automaticBrush : automaticBrushes) {
                    instructions = automaticBrush.draw(instructions);
                }
                List<GraphicInstructions> instructionList = new ArrayList<>();
                instructionList.add(instructions);
                sgi.setInstructionList(workerName, instructionList);
                status.done();
            }
        }
        System.out.println(workerName + " dead");
    }

    public void dismiss() { running = false; }
}
