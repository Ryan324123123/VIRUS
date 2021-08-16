import javax.swing.*;
import java.awt.*;

public class Person extends JComponent
{
    private double thisXPos; //x Position
    private double thisYPos; //y Position
    private double thisXVel; //x Velocity
    private double thisYVel; //y Velocity
    private double infectionLevel = 0; //value of how infected a person is, 0 = not and then other +ve numbers are a scale of sickness
    private int infectionProgress = INFECTION_DURATION; //defaults to uninfected value which is the duration of an infection
    private final double scaleFactor;
    private static final int INFECTION_DURATION = 30;
    private static final int RADIUS = 30;
    private static final double MAX_INFECTION = 1.125;
    private static final double infectionRingWidth = 6;
    private int timesInfected = 0;
    private int timesRecovered = 0;

    public Person(double scaleFactor){
        this.scaleFactor = scaleFactor;
        setSize((int) (RADIUS*2*scaleFactor),(int) (RADIUS*2*scaleFactor)); //this tells the GUI module, JComponents, the size of the people which it uses behind the scenes
    }

    public void setInfected(){
        timesInfected ++;
        infectionProgress = 0;
    }
    public void infectionEvolution(){
        if(infectionProgress<INFECTION_DURATION) { //INFECTION_DURATION is the duration of the infection, if a person is not infected their infection time sits at INFECTION_DURATION
            infectionLevel = -0.005 * Math.pow(infectionProgress, 2) + 0.15 * infectionProgress;
            infectionProgress ++;
        } else {
            if(infectionProgress>30){
                timesRecovered ++;
            }
            infectionLevel = 0;
        }
    }
    public double getRadius(){ return RADIUS*scaleFactor; }
    public double getInfectionLevel(){ return infectionLevel; }
    public double getXVelocity(){ return thisXVel; }
    public double getYVelocity(){ return thisYVel; }
    public double getXCoordinate(){ return thisXPos; }
    public double getYCoordinate(){ return thisYPos; }
    public void setXVel(double newXVel){ thisXVel = newXVel; }
    public void setYVel(double newYVel){ thisYVel = newYVel; }
    public void setXCoordinate(double newXPos){ thisXPos = newXPos; }
    public void setYCoordinate(double newYPos){ thisYPos = newYPos; }
    public int getTimesInfected(){return timesInfected; }
    public int getTimesRecovered(){return timesRecovered; }

    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        if(infectionProgress != 30){
            graphics.setColor(Color.blue);
            graphics.fillOval(1, 1, (int) (RADIUS*scaleFactor), (int) (RADIUS*scaleFactor));
        }
        graphics.setColor(new Color((int)(255* infectionLevel / MAX_INFECTION), (int) (255*(1- infectionLevel / MAX_INFECTION)), 0));
        graphics.fillOval((int) (1 + 0.5*infectionRingWidth), (int) (1 + 0.5*infectionRingWidth), (int) ((RADIUS-infectionRingWidth)*scaleFactor), (int) ((RADIUS-infectionRingWidth)*scaleFactor));
    }
}