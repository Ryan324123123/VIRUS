import javax.swing.*;
import java.awt.*;

public class Simulation extends JPanel
{
    private final Person[] people;
    private static final int P_NUM = 100;
    private static final int FRAME_TIME = 20;  //processing can take from 0 - 25 ms ish depending on some parameters
    private final Graph totalInfectionsGraph;
    private int cycleCount=0;

    public Simulation()
    {
        people = new Person[P_NUM];
        for(int i=0; i<P_NUM; i++){
            people[i] = new Person();
            Person Person = people[i];
            add(Person);
        }
        people[0].setInfected();
        people[1].setInfected();
        
        setDoubleBuffered(true);
        setLayout(null);
        totalInfectionsGraph = new Graph("Graph 1", "time(s)", "total infectedness", "total Infected", 0,0);
    }

    private void update()
    {
        double totalVirus = 0;
        for(int i=0; i<P_NUM; i++){
            people[i].infectionEvolution();
            totalVirus += people[i].getInfectionLevel();
            movePerson(i);
        }
        Collide();
        cycleCount ++;
        totalInfectionsGraph.addNewData(cycleCount,totalVirus);
    }

    public Person getPerson(int i){
        return people[i];
    }
    
    private void Collide()
    {
        //NTS: peoples infection level doesn't start increasing until after they have left their infector
        //pretend that I made it such that two infected people can interact but I then decided that it was sub optimal and so reverted my code
        for(int i = 0; i<P_NUM; i++){
            for(int j = i+1; j<P_NUM; j++){
                Person infector = null;
                Person infectee = null;

                if(people[i].getInfectionLevel() != 0 && people[j].getInfectionLevel() == 0 ){
                    infector = people[i];
                    infectee = people[j];
                }
                if(people[i].getInfectionLevel() == 0 && people[j].getInfectionLevel() != 0 ){
                    infector = people[j];
                    infectee = people[i]; 
                }
                if(infector != null){
                    double distance = Math.sqrt(Math.pow(infector.getXCoordinate()-infectee.getXCoordinate(),2)+Math.pow(infector.getYCoordinate()-infectee.getYCoordinate(),2));
                    if(distance <= people[0].getRadius()*1.1){
                        infectee.setInfected();
                    }
                }
            }
        }
    }
    private void movePerson(int i){        
        //get the pos and vel of the person this method is moving
        double xVel = people[i].getXVelocity();
        double yVel = people[i].getYVelocity();
        //Person movement Variables
        double xPos = people[i].getXCoordinate();
        double yPos = people[i].getYCoordinate();
        double xBound = people[0].getXBound();  //memory leak?
        double yBound = people[0].getYBound();

        double xAcc = 0;
        double yAcc = 0;
        //if statements for wall boundaries //
        if(people[i].getXCoordinate()> people[0].getXBound() ){
            xAcc =+ -2*(1+people[i].getXCoordinate()-people[0].getXBound());  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(people[i].getXCoordinate() < 0){
            xAcc =+ -2*(-1+people[i].getXCoordinate());
        }
        if(people[i].getYCoordinate()> people[0].getYBound() ){
            yAcc =+ -2*(1+people[i].getYCoordinate()-people[0].getYBound());  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(people[i].getYCoordinate() < 0){
            yAcc =+ -2*(-1+people[i].getYCoordinate());
        }
        //avoidance of other people//
        
        
        //main acceleration field//
        xAcc += (  Math.sin( xBound*(xPos +xBound/2)-(yPos +yBound/2) ) );
        yAcc += (  Math.cos( yBound*(xPos +xBound/2)+(yPos +yBound/2) ) );


        //drag acting on people, slows them down. (restricts the growth of velocity)
        xVel = xVel *(1-0.03);
        yVel = yVel *(1-0.03);

        //update variable and send back to person
        xVel += xAcc;
        yVel += yAcc;
        xPos += xVel;
        yPos += yVel;
        people[i].setXVel(xVel);
        people[i].setYVel(yVel);
        people[i].setXCoordinate(xPos);
        people[i].setYCoordinate(yPos);
        
    }
    
    
    public void paintComponent(Graphics graphics)
    {
        long frameStartTime = System.nanoTime();
        super.paintComponent(graphics);
        update();
        for(Person person : people) {
            person.paintComponent(graphics);
        }
        long frameEndTime = System.nanoTime();
        long processingTime = (frameEndTime - frameStartTime)/1000000;
        try {
            if(processingTime<FRAME_TIME){
            Thread.sleep(FRAME_TIME-processingTime);
            }
        } catch (InterruptedException error){
            error.printStackTrace();
        }
        System.out.println(processingTime);
        repaint();
    }
}
