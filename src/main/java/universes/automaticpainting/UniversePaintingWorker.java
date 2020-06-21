package universes.automaticpainting;

import gui.GraphicInstructions;

import java.util.List;

public class UniversePaintingWorker extends Thread {

    private WorkerStatus status;
    private boolean running = false;


    public void setStatus(WorkerStatus status) {
        this.status = status;
    }


    public void run() {
        System.out.println("Worker Running");
        running = true;
        while (running) {
            if (!status.isDone()) {
                GraphicInstructions instructions = status.getInstructions();
                List<AutomaticBrush> automaticBrushes = status.getAutomaticBrushes();
                for (AutomaticBrush automaticBrush : automaticBrushes) {
                    instructions = automaticBrush.draw(instructions);
                }
                status.done();
            }
        }
        System.out.println("Worker dead");
    }

    public void dismiss() { running = false; }
}
