package universes.automaticpainting;

import gui.GraphicInstructions;

import java.util.Random;

public class AutomaticBrush {

    private Random rand;

    private int width;
    private int height;

    private double r;
    private double g;
    private double b;
    private double a;

    private double x = 600;
    private double xSpeed = 0;
    private double y = 300;
    private double ySpeed = 0;
    private double maxSpeed = 0.05;

    private int size = 5;

    private double atenuatorOffset;
    private double maxSpeedOffset;

    public AutomaticBrush(int x, int y, Random rand, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rand = rand;
        r = rand.nextDouble()*255;
        g = rand.nextDouble()*255;
        b = rand.nextDouble()*255;
        atenuatorOffset = rand.nextDouble()*100;
        maxSpeedOffset = rand.nextDouble()*0.2;
        maxSpeed += maxSpeedOffset;
    }

    public GraphicInstructions draw(GraphicInstructions instructions) {
        double atenuator = (atenuatorOffset + rand.nextDouble()*100);

        xSpeed+=(rand.nextDouble()-0.5)/atenuator;
        if (xSpeed>maxSpeed) xSpeed = maxSpeed;
        if (xSpeed<-maxSpeed) xSpeed = -maxSpeed;
        x+=xSpeed;
        x = x > (width-size) ? x-width-size : x;
        x = x < 0 ? width-size-x : x;

        ySpeed+=(rand.nextDouble()-0.5)/atenuator;
        if (ySpeed>maxSpeed) ySpeed = maxSpeed;
        if (ySpeed<-maxSpeed) ySpeed = -maxSpeed;
        y+=ySpeed;
        y = y > (height-size) ? y-height-size : y;
        y = y < 0 ? height-size-y : y;


        r += (rand.nextDouble()-0.5)/(1+rand.nextDouble());
        g += (rand.nextDouble()-0.5)/(1+rand.nextDouble());
        b += (rand.nextDouble()-0.5)/(1+rand.nextDouble());
        r = r > 255 ? 255 : r;
        g = g > 255 ? 255 : g;
        b = b > 255 ? 255 : b;
        r = r < 0 ? 0 : r;
        g = g < 0 ? 0 : g;
        b = b < 0 ? 0 : b;
        instructions.addCircle((int)x,(int)y,size,size,255,(int)r,(int)g,(int)b,true);

        return instructions;
    }
}
