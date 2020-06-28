package universes.automaticpainting;

import gui.GraphicInstructions;

import java.util.List;

public class WorkerStatus {
    private volatile boolean isDone = true;

    public void work() {
        this.isDone = false;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public void done() {
        this.isDone = true;
    }
}
