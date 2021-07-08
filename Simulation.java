/**
 * Write a description of class Main here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import javax.swing.*;
import java.awt.*;
import java.text.*;
public class Simulation extends JPanel
{
    private Person[] people;
    int timeMax = 200; //the max time that the simulation runs for (these dont have to be user inputs as they are of abetery scale. just use a good value that produces quick, intresting values.)
    private final int P_NUM = 50;
    private final int FRAME_TIME = 10;  //processing can take up to 10 ms ish 
    private final DecimalFormat myFormat;
    
    
    //Person movement Variables
    private double Xpos;
    private double Ypos;
    private double Xvel;
    private double Yvel;
    private double Xacc;
    private double Yacc;
    
    public Simulation()
    {
        people = new Person[P_NUM];
        myFormat = new DecimalFormat("#.###");
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

                if(people[i].getInfectedness() != 0 && people[j].getInfectedness() == 0 ){
                    infector = people[i];
                    infectee = people[j];
                }
                if(people[i].getInfectedness() == 0 && people[j].getInfectedness() != 0 ){
                    infector = people[j];
                    infectee = people[i]; 
                }
                if(infector != null){
                    double distance = Math.sqrt(Math.pow(infector.getXcoord()-infectee.getXcoord(),2)+Math.pow(infector.getYcoord()-infectee.getYcoord(),2));
                    if(distance <= people[0].getRadius() + 20){
                        infectee.setInfected();
                    }
                }

            }
        }
    }
    private void movePerson(int i){        
        //get the pos and vel of the person this method is moving
        Xvel = people[i].getXvel();
        Yvel = people[i].getYvel();
        Xpos = people[i].getXcoord();
        Ypos = people[i].getYcoord();
        double Xbound = people[0].getXbound();  //memory leak?
        double Ybound = people[0].getYbound();
        
        Xacc = 0;
        Yacc = 0;
        //if statements for wall boundaries 
        if(people[i].getXcoord()> people[0].getXbound() ){
            Xacc =+ -2*(1+people[i].getXcoord()-people[0].getXbound());  //the acceleration due to being out of bouncds is a function of how far out it is.
        }else if(people[i].getXcoord() < 0){
            Xacc =+ -2*(-1+people[i].getXcoord());
        }
        if(people[i].getYcoord()> people[0].getYbound() ){
            Yacc =+ -2*(1+people[i].getYcoord()-people[0].getYbound());  //the acceleration due to being out of bouncds is a function of how far out it is.
        }else if(people[i].getYcoord() < 0){
            Yacc =+ -2*(-1+people[i].getYcoord());
        }
        //avoidance of other people
        
        
        //main acceleration field
        Xacc += (  Math.sin( 10*Xbound*(Xpos+Xbound/2)-(Ypos+Ybound/2) ) );
        //(sin(15 (x + y)) + 1) / 15 - 0.25 (x - 0.5)
        Yacc += (  Math.cos( 10*Ybound*(Xpos+Xbound/2)+(Ypos+Ybound/2) ) );
        //(cos(15 (x - y)) + 1) / 15 - 0.25 (y - 0.5)
        
        //drag acting on people, slows them down. (restricts the growth of velocity)
        Xvel = Xvel*(1-0.05);
        Yvel = Yvel*(1-0.05);
        //update variable and send back to person
        Xvel += Xacc;
        Yvel += Yacc;
        Xpos += Xvel;
        Ypos += Yvel;
        people[i].setXvel(Xvel);
        people[i].setYvel(Yvel);
        people[i].setXcoord(Xpos);
        people[i].setYcoord(Ypos);
        
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
