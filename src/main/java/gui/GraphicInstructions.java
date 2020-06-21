package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphicInstructions {

    private volatile LinkedList<Map<String,Object>> instructions;

    public GraphicInstructions() {
        this.instructions = new LinkedList<>();
    };

    public List<Map<String,Object>> getInstructionMaps() { return instructions; }

    public boolean addAll(List<Map<String,Object>> newInstructions) {
        return(instructions.addAll(newInstructions));
    }

    public void addCircle(int x, int y, int width, int height, int a, int r, int g, int b, boolean filled) {
        Map<String,Object> instruction = new HashMap<>();
        instruction.put("type", InstructionTypes.CIRCLE);
        instruction.put("x", x);
        instruction.put("y", y);
        instruction.put("width", width);
        instruction.put("height", height);
        instruction.put("a", a);
        instruction.put("r", r);
        instruction.put("g", g);
        instruction.put("b", b);
        instruction.put("filled", filled);
        instructions.add(instruction);
    };

    public void addBox(int x, int y, int width, int height, int a, int r, int g, int b, boolean filled) {
        Map<String,Object> instruction = new HashMap<>();
        instruction.put("type", InstructionTypes.BOX);
        instruction.put("x", x);
        instruction.put("y", y);
        instruction.put("width", width);
        instruction.put("height", height);
        instruction.put("a", a);
        instruction.put("r", r);
        instruction.put("g", g);
        instruction.put("b", b);
        instruction.put("filled", filled);
        instructions.add(instruction);
    };

    public void addLine(int x1, int y1, int x2, int y2, int a, int r, int g, int b) {
        Map<String,Object> instruction = new HashMap<>();
        instruction.put("type", InstructionTypes.LINE);
        instruction.put("x1", x1);
        instruction.put("y1", y1);
        instruction.put("x2", x2);
        instruction.put("y2", y2);
        instruction.put("a", a);
        instruction.put("r", r);
        instruction.put("g", g);
        instruction.put("b", b);
        instructions.add(instruction);
    };

    public void addPixel(int x, int y, int a, int r, int g, int b) {
        Map<String,Object> instruction = new HashMap<>();
        instruction.put("type", InstructionTypes.PIXEL);
        instruction.put("x", x);
        instruction.put("y", y);
        instruction.put("a", a);
        instruction.put("r", r);
        instruction.put("g", g);
        instruction.put("b", b);
        instructions.add(instruction);
    };

}
