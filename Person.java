

/**
 * Write a description of class Person here.
 *
 * @author (RYAN)
 * @version (Current version)
 */
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;
import java.awt.*;
public class Person extends JComponent
{
    private double thisXPos; //x Position
    private double thisYPos; //y Position
    private double thisXVel; //x Velocity
    private double thisYVel; //y Velocity
    private double infectedness; //value of how infectd a peoson is, 0 = not and then other +ve numebrs are a scale of sickness
    private int infectionProgress = 30; //defaults to uninfected value
    private double resistance;
    private double virusStrain; 
    
    /**
     * TODO:
     * implement resistance, virusStrain and the variable inection chance
     * have graph anilitics
     */
    
    private static final int INFECTION_DURATION = 30;
    private static final int RADIUS = 32;
    private static final int X_BOUND_MAX = 800;
    private static final int Y_BOUND_MAX = 800;
    private static final double MAX_INFECTION = 1.125;
    public Person(){
        thisXPos = ThreadLocalRandom.current().nextDouble(0, X_BOUND_MAX);
        thisYPos = ThreadLocalRandom.current().nextDouble(0, Y_BOUND_MAX);
        
    }
    public void setInfected(){
        infectionProgress = 0;
    }
    public void infectionEvolution(){
        if(infectionProgress<INFECTION_DURATION) { //INFECTION_DURATION is the duration of the infection, if a person is not infected their infection time sits at INFECTION_DURATION
            infectedness = -0.005 * Math.pow(infectionProgress, 2) + 0.15 * infectionProgress; 
            infectionProgress ++;
        } else {
            infectedness = 0;
        }
    }
    public double getInfectedness(){ return infectedness; }
    public int getXbound(){ return X_BOUND_MAX; }
    public int getYbound(){ return Y_BOUND_MAX; }
    public double getXvel(){ return thisXVel; }
    public double getYvel(){ return thisYVel; }
    public double getXcoord(){ return thisXPos; }
    public double getYcoord(){ return thisYPos; }       
    public int getRadius(){ return RADIUS; }
    public double getMaxInfectedness(){return MAX_INFECTION; }
    
    public void setXvel(double newXvel){ thisXVel = newXvel; }
    public void setYvel(double newYvel){ thisYVel = newYvel; }
    public void setXcoord(double newXPos){ thisXPos = newXPos; }
    public void setYcoord(double newYPos){ thisYPos = newYPos; }    
    
    
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        setLocation((int) (thisXPos * 100), (int) (thisYPos * 100));
        if(infectionProgress != 30){
            graphics.setColor(Color.blue);
            graphics.fillOval((int) (thisXPos), (int) (thisYPos), RADIUS, RADIUS);
        }
        graphics.setColor(new Color((int)(255*infectedness/ MAX_INFECTION), (int) (255*(1-infectedness/ MAX_INFECTION)), 0));
        graphics.fillOval((int) (thisXPos+3), (int) (thisYPos+3), RADIUS-6, RADIUS-6);
    }
    
    
}
