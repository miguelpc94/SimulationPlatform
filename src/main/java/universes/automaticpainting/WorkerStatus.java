package universes.automaticpainting;

import gui.GraphicInstructions;

import java.util.List;

public class WorkerStatus {
    private volatile boolean isDone = true;
    private volatile List<AutomaticBrush> automaticBrushes;
    private volatile GraphicInstructions instructions;

    public void work(List<AutomaticBrush> automaticBrushes) {
        this.automaticBrushes = automaticBrushes;
        this.instructions = new GraphicInstructions();
        this.isDone = false;
    }

    public List<AutomaticBrush> getAutomaticBrushes() {
        return this.automaticBrushes;
    }

    public GraphicInstructions getInstructions() {
        return this.instructions;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public void done() {
        this.isDone = true;
    }
}
