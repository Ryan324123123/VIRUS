import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

public class Simulation extends JPanel
{
    private final Person[] people;
    private final int P_NUM;
    private static final int FRAME_TIME = 20;  //processing can take from 0 - 25 ms ish depending on some parameters
    private final Graph totalInfectionsGraph;
    private int cycleCount=0;
    private final int X_BOUND_MAX=500;
    private final int Y_BOUND_MAX=450;
    public Simulation()
    {
        Scanner commandLineInput = new Scanner(System.in);
        System.out.println("enter the population density you want (1 is the least, 10 is the max.)");
        //by using population density, calculate the amount of people to simulate for.
        //while the userInput is not in the 1-10 range, keep asking for the input.
        int userInput = commandLineInput.nextInt();
        while(userInput<0 || userInput>10){ //todo: input of 0 or a causes error, 11 triggers the ask again, fix.
            System.out.println("Invalid input, the range for population density is 1 - 10. Try again.");
            userInput = commandLineInput.nextInt();
        }
        P_NUM = Math.floorDiv(userInput*X_BOUND_MAX*Y_BOUND_MAX, 20000);

        people = new Person[P_NUM];
        System.out.println(P_NUM);
        for(int i=0; i<P_NUM; i++){
            people[i] = new Person();
            people[i].setXCoordinate(ThreadLocalRandom.current().nextDouble(0, X_BOUND_MAX));
            people[i].setYCoordinate(ThreadLocalRandom.current().nextDouble(0, Y_BOUND_MAX));
            Person Person = people[i];
            add(Person);
        }
        people[0].setInfected();
        people[1].setInfected();
        
        setDoubleBuffered(true);
        setLayout(null);
        totalInfectionsGraph = new Graph("Graph 1", "time(s)", "total infectedness", "total Infected", 0,0);
    }
    public Person getPerson(int i){return people[i];}

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

        double xAcc = 0;
        double yAcc = 0;
        //if statements for wall boundaries //
        if(people[i].getXCoordinate()> X_BOUND_MAX ){
            xAcc =+ -2*(1+people[i].getXCoordinate()-X_BOUND_MAX);  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(people[i].getXCoordinate() < 0){
            xAcc =+ -2*(-1+people[i].getXCoordinate());
        }
        if(people[i].getYCoordinate()> Y_BOUND_MAX){
            yAcc =+ -2*(1+people[i].getYCoordinate()-Y_BOUND_MAX);  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(people[i].getYCoordinate() < 0){
            yAcc =+ -2*(-1+people[i].getYCoordinate());
        }
        //avoidance of other people//


        //main acceleration field//
        xAcc += (  Math.sin( X_BOUND_MAX*(xPos +X_BOUND_MAX/2)-(yPos +Y_BOUND_MAX/2) ) );
        yAcc += (  Math.cos( Y_BOUND_MAX*(xPos +X_BOUND_MAX/2)+(yPos +Y_BOUND_MAX/2) ) );


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
    public int getX_BOUND_MAX(){return X_BOUND_MAX;}
    public int getY_BOUND_MAX(){return Y_BOUND_MAX;}
    
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
        System.out.println(processingTime+" "+(FRAME_TIME-processingTime));
        repaint();
    }
}
