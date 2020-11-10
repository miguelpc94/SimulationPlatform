package gui;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimulationGraphicsInterface {

    private int width;
    private int height;

    private volatile BufferedImage bufferedImage;
    private volatile Map<String,List<GraphicInstructions>> instructionList;
    private volatile KeyHandler keyHandler;


    public SimulationGraphicsInterface(int width, int height) {
        this.width = width;
        this.height = height;
        instructionList = new HashMap<>();
    }

    public void setKeyHandler(KeyHandler keyHandler) {
        this.keyHandler = keyHandler;
    }

    public void setBufferedImage(BufferedImage bufferedImage) { this.bufferedImage = bufferedImage; }

    public BufferedImage getBufferedImage() { return bufferedImage; }

    public boolean isKeyCharPressed(char key) {
        return keyHandler != null && keyHandler.isKeyCharPressed(key);
    }

    public void setInstructionList(String listName,List<GraphicInstructions> newInstructions) {
        instructionList.put(listName,newInstructions);
    }

    public Set<String> getInstructionListNames() {
        return this.instructionList.keySet();
    }

    public List<GraphicInstructions> getInstructions(String listName) {
        if (!instructionList.containsKey(listName)) return null;
        return instructionList.get(listName);
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}

}
