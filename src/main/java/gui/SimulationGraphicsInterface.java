package gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Add methods to set and get a BufferedImage from the GUI as well as user input <======== 4 (head)
public class SimulationGraphicsInterface {

    private int width;
    private int height;

    private List<GraphicInstructions> instructionList;
    private Map<String,Integer> userInput;

    public SimulationGraphicsInterface(int width, int height) {
        this.width = width;
        this.height = height;
        instructionList = null;
        userInput = new HashMap<>();
    }

    public void setUserInput(Map<String,Integer> newUserInput) {
        userInput = newUserInput;
    }

    public Map<String,Integer> getUserInput() {
        return userInput;
    }

    public void setInstructionList(List<GraphicInstructions> newInstructions) {
        instructionList = newInstructions;
    }

    public List<GraphicInstructions> getInstructions() {
        return instructionList;
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}

}
