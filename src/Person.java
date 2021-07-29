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
    private static final double infectionBandWidth = 6;

    public Person(double scaleFactor){
        this.scaleFactor = scaleFactor;
    }

    public void setInfected(){
        infectionProgress = 0;
    }
    public void infectionEvolution(){
        if(infectionProgress<INFECTION_DURATION) { //INFECTION_DURATION is the duration of the infection, if a person is not infected their infection time sits at INFECTION_DURATION
            infectionLevel = -0.005 * Math.pow(infectionProgress, 2) + 0.15 * infectionProgress;
            infectionProgress ++;
        } else {
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
    
    
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        setLocation((int) (thisXPos * 100), (int) (thisYPos * 100));
        if(infectionProgress != 30){
            graphics.setColor(Color.blue);
            graphics.fillOval((int) (thisXPos), (int) (thisYPos), (int) (RADIUS*scaleFactor), (int) (RADIUS*scaleFactor));
        }
        graphics.setColor(new Color((int)(255* infectionLevel / MAX_INFECTION), (int) (255*(1- infectionLevel / MAX_INFECTION)), 0));
        graphics.fillOval((int) (thisXPos+infectionBandWidth*scaleFactor*0.5), (int) (thisYPos+infectionBandWidth*scaleFactor*0.5), (int) ((RADIUS-infectionBandWidth)*scaleFactor), (int) ((RADIUS-infectionBandWidth)*scaleFactor));
    }
    
    
}
