import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Person extends JComponent
{
    private double thisXPos; //x Position
    private double thisYPos; //y Position
    private double thisXVel; //x Velocity
    private double thisYVel; //y Velocity
    private double infectionLevel = 0; //value of how infected a person is, 0 = not and then other +ve numbers are a scale of sickness
    private int infectionProgress = INFECTION_DURATION; //defaults to uninfected value which is the duration of an infection
    private final double scaleFactor; //this variable represents how much to shrink the people and increase the number of people due to the small size
    private final double humanResponseFactor; //this is how a persons immunity changes after getting infected
    private static final int INFECTION_DURATION = 30;
    private static final int DIAMETER = 30;
    private static final double MAX_INFECTION = 1.125; //this is the max infectivity a person can have, this is used for normalisation purposes
    private static final double infectionRingWidth = DIAMETER /5.0;
    private double virusPotency;
    private double virusImmunity;

    public Person(double scaleFactor){
        humanResponseFactor = ThreadLocalRandom.current().nextDouble(0.5, 1.3);
        this.scaleFactor = scaleFactor;
        setSize((int) (DIAMETER *2*scaleFactor),(int) (DIAMETER *2*scaleFactor)); //this tells the GUI module, JComponents, the size of the people which it uses behind the scenes
    }

    public void setInfected(double infectorVirusPotency, double virusMutationRate){ //when a person is successfully infected their immunity and virus potency gets updated with new values.
        setVirusImmunity(infectorVirusPotency*humanResponseFactor);
        setVirusPotency(infectorVirusPotency*virusMutationRate);
        infectionProgress = 0; //their infection progress also gets reset and so after each cycle it goes up by 1 until they get better
    }
    public void infectionEvolution(){ //this code 'evolves' how infected the person is, this only happens if they are within the infection time period
        if(infectionProgress<INFECTION_DURATION) { //INFECTION_DURATION is the duration of the infection, if a person is not infected their infection time sits at INFECTION_DURATION
            infectionLevel = -0.005 * Math.pow(infectionProgress, 2) + 0.15 * infectionProgress;
            infectionProgress ++;
        } else {
            infectionLevel = 0;
        }
    }
    public double getDiameter(){ return DIAMETER *scaleFactor; }  //these get methods are so simulation can read the variables in person.
    public double getInfectionLevel(){ return infectionLevel; }
    public double getXVelocity(){ return thisXVel; }
    public double getYVelocity(){ return thisYVel; }
    public double getXCoordinate(){ return thisXPos; }
    public double getYCoordinate(){ return thisYPos; }
    public double getVirusPotency(){ return virusPotency; }
    public double getVirusImmunity(){ return virusImmunity; }
    public void setXVel(double newXVel){ thisXVel = newXVel; }
    public void setYVel(double newYVel){ thisYVel = newYVel; }
    public void setXCoordinate(double newXPos){ thisXPos = newXPos; }
    public void setYCoordinate(double newYPos){ thisYPos = newYPos; }
    public void setVirusPotency(double newVirusPotency) { virusPotency = newVirusPotency; }
    public void setVirusImmunity(double newVirusImmunity) { virusImmunity = newVirusImmunity; }



    public void paintComponent(Graphics graphics)  //paintComponents describes how to draw each person and this gets called on each update in simulation.
    {
        super.paintComponent(graphics);
        graphics.setColor(new Color((int)(255* infectionLevel / MAX_INFECTION), (int) (255*(1- infectionLevel / MAX_INFECTION)), 0));
        graphics.fillOval(1, 1, (int) (DIAMETER *scaleFactor), (int) (DIAMETER *scaleFactor));
        graphics.setColor(Color.orange);
        graphics.fillOval((int) ((1 + scaleFactor*infectionRingWidth)), (int) ((1 + scaleFactor*infectionRingWidth)), (int) (scaleFactor*(DIAMETER -2*infectionRingWidth)), (int) (scaleFactor*(DIAMETER -2*infectionRingWidth)));
    }
}