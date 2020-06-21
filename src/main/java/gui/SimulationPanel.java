package gui;

import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SimulationPanel extends JPanel implements Runnable {

    private int width;
    private int height;

    private Random rand;

    private Thread thread;
    private boolean running = false;
    private SimulationGraphicsInterface sgi;

    final double RENDER_HERTZ =500;
    final double TBR = 1000000000/RENDER_HERTZ; // Time Before Render

    private BufferedImage img;
    private Graphics2D graphics2D;

    // A state needs to be passed to this Panel, so it can update the drawing in the update part of the cycle
    public SimulationPanel(SimulationGraphicsInterface sgi) {
        this.width = sgi.getWidth();
        this.height = sgi.getHeight();
        this.sgi = sgi;
        setPreferredSize(new Dimension(width,height));
        setFocusable(true);
        requestFocus();
        this.rand = new Random(12);
    }

    public void addNotify() {
        super.addNotify();

        if (thread == null) {
            thread = new Thread(this,"simulation-gui");
            thread.start();
        }
    }

    public void init() {
        running = true;

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = (Graphics2D) img.getGraphics(); // Allows to draw on the buffered image

    }

    public void run() {
        init();

        double lastRenderTime = System.nanoTime();

        while (running) {
            update();
            input();
            double now = System.nanoTime();
            while ((now-lastRenderTime) < TBR) {
                long millisToSleep = (long)((TBR - (now-lastRenderTime))/1000000);
                if (millisToSleep>0) {
                    Thread.yield();
                    try {
                        Thread.sleep(millisToSleep/2);
                    } catch (Exception e) {
                        System.out.println("Error yielding render thread");
                    }
                }
                now = System.nanoTime();
            }
            double timeBetweenRenderings = System.nanoTime() - lastRenderTime;
            lastRenderTime = System.nanoTime();
            render();
            draw();
            //System.out.println("FPS:" + (1/(timeBetweenRenderings/1000000000)));
        }
    }


    public void update() {

    }

    public void input() {
    }


    public void render() {

        List<GraphicInstructions> instructionList = sgi.getInstructions();

        if (instructionList != null && graphics2D != null) {
            for(GraphicInstructions instructions : instructionList) {
                for (Map<String, Object> instructionMap : instructions.getInstructionMaps()) {
                    switch ((InstructionTypes) instructionMap.get("type")) {
                        case CIRCLE:
                            graphics2D.setColor(new Color((int) instructionMap.get("r"), (int) instructionMap.get("g"), (int) instructionMap.get("b"), (int) instructionMap.get("a")));
                            if ((boolean) instructionMap.get("filled")) {
                                graphics2D.fillOval((int) instructionMap.get("x"), (int) instructionMap.get("y"), (int) instructionMap.get("width"), (int) instructionMap.get("height"));
                            } else {
                                graphics2D.drawOval((int) instructionMap.get("x"), (int) instructionMap.get("y"), (int) instructionMap.get("width"), (int) instructionMap.get("height"));
                            }
                            break;
                        case BOX:
                            graphics2D.setColor(new Color((int) instructionMap.get("r"), (int) instructionMap.get("g"), (int) instructionMap.get("b"), (int) instructionMap.get("a")));
                            if ((boolean) instructionMap.get("filled")) {
                                graphics2D.fillRect((int) instructionMap.get("x"), (int) instructionMap.get("y"), (int) instructionMap.get("width"), (int) instructionMap.get("height"));
                            } else {
                                graphics2D.drawRect((int) instructionMap.get("x"), (int) instructionMap.get("y"), (int) instructionMap.get("width"), (int) instructionMap.get("height"));
                            }
                            break;
                        case LINE:
                            graphics2D.setColor(new Color((int) instructionMap.get("r"), (int) instructionMap.get("g"), (int) instructionMap.get("b"), (int) instructionMap.get("a")));
                            graphics2D.drawLine((int) instructionMap.get("x1"), (int) instructionMap.get("y1"), (int) instructionMap.get("x2"), (int) instructionMap.get("y2"));
                            break;
                        case PIXEL:
                            graphics2D.setColor(new Color((int) instructionMap.get("r"), (int) instructionMap.get("g"), (int) instructionMap.get("b"), (int) instructionMap.get("a")));
                            graphics2D.drawRect((int) instructionMap.get("x"), (int) instructionMap.get("y"), 1, 1);
                            break;
                        default:
                    }
                }
            }
            sgi.setInstructionList(null);
        }
    }

    public void draw() {
        Graphics graphics = (Graphics) this.getGraphics();
        graphics.drawImage(img, 0, 0, width, height, null);
        graphics.dispose();
    }
}
