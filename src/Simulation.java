import javax.swing.*;
import java.awt.*;

public class Simulation extends JPanel
{
    private final Person[] people;
    private final int P_NUM = 50;
    private static final int FRAME_TIME = 10;  //processing can take up to 10 ms ish

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
    }

    private void update()
    {
        for(int i=0; i<P_NUM; i++){
            //System.out.print(myFormat.format(people[i].getInfectedness()/people[i].getMaxInfectedness())+ "   ");
            people[i].infectionEvolution();
            movePerson(i);
            Collide();
        }
        //System.out.println("");
    }

    public Person getPerson(int i){
        return people[i];
    }
    
    private void Collide()
    {
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
                    if(distance <= people[0].getRadius() + 20){
                        infectee.setInfected();
                    }
                }

            }
        }
    }
    private void movePerson(int i){        
        //get the pos and vel of the person this method is moving
        double xvel = people[i].getXVelocity();
        double yvel = people[i].getYVelocity();
        //Person movement Variables
        double xpos = people[i].getXCoordinate();
        double ypos = people[i].getYCoordinate();
        double Xbound = people[0].getXBound();  //memory leak?
        double Ybound = people[0].getYBound();

        double xacc = 0;
        double yacc = 0;
        //if statements for wall boundaries 
        if(people[i].getXCoordinate()> people[0].getXBound() ){
            xacc =+ -2*(1+people[i].getXCoordinate()-people[0].getXBound());  //the acceleration due to being out of bouncds is a function of how far out it is.
        }else if(people[i].getXCoordinate() < 0){
            xacc =+ -2*(-1+people[i].getXCoordinate());
        }
        if(people[i].getYCoordinate()> people[0].getYBound() ){
            yacc =+ -2*(1+people[i].getYCoordinate()-people[0].getYBound());  //the acceleration due to being out of bouncds is a function of how far out it is.
        }else if(people[i].getYCoordinate() < 0){
            yacc =+ -2*(-1+people[i].getYCoordinate());
        }
        //avoidance of other people
        
        
        //main acceleration field
        xacc += (  Math.sin( 10*Xbound*(xpos +Xbound/2)-(ypos +Ybound/2) ) );
        //(sin(15 (x + y)) + 1) / 15 - 0.25 (x - 0.5)
        yacc += (  Math.cos( 10*Ybound*(xpos +Xbound/2)+(ypos +Ybound/2) ) );
        //(cos(15 (x - y)) + 1) / 15 - 0.25 (y - 0.5)
        
        //drag acting on people, slows them down. (restricts the growth of velocity)
        xvel = xvel *(1-0.05);
        yvel = yvel *(1-0.05);
        //update variable and send back to person
        xvel += xacc;
        yvel += yacc;
        xpos += xvel;
        ypos += yvel;
        people[i].setXvel(xvel);
        people[i].setYvel(yvel);
        people[i].setXcoord(xpos);
        people[i].setYcoord(ypos);
        
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
