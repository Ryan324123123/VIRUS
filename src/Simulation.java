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
    private final int X_BOUND_MAX=500;  //todo: next step is to have this work for any given window its put in. (adds resize compatibility)
    private final int Y_BOUND_MAX=500;
    private final double infectChance;
    public Simulation()
    {
        setLayout(new GridLayout(1,2));
        JPanel motionSpace = new JPanel();
        motionSpace.setLayout(null);
        add(motionSpace);

        Scanner commandLineInput = new Scanner(System.in);
        //these three variables are used for userInput checking
        int userInputInt;
        double userInputDouble;
        boolean loopAgain;

        //by getting the area they wish to simulate I can then scale the people to fit so they are the correct corresponding size.
        System.out.println("Enter the open space area that you wish to simulate (250,000 is normal)");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextInt()){ //this catches non-integer values
                System.out.println("Invalid input, please enter a value less than ___.");
                commandLineInput.next();
            }
            userInputInt=commandLineInput.nextInt();
            if(userInputInt>0){
                loopAgain = false;
            }else{
                System.out.println("Invalid input, please enter a value less than 10000");
            }
        }while(loopAgain);
        double scaleFactor = 500/Math.sqrt(userInputInt); //this gets passed to each person to scale how large they are in the window

        //by using population density, calculate the amount of people to simulate for.
        //while the userInput is not in the 1-10 range, keep asking for the input.
        System.out.println("Enter the population density you want (1 is the least, 10 is the max.)");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextInt()){ //this catches non-integer values
                System.out.println("Invalid input, please enter an integer between 1 - 10.");
                commandLineInput.next();
            }
            userInputInt=commandLineInput.nextInt();
            if(userInputInt<=10 && userInputInt>0){
                loopAgain = false;
            }else{
                System.out.println("Invalid input, please enter an integer between 1 - 10.");
            }
        }while(loopAgain);


        P_NUM = (int) ((userInputInt*X_BOUND_MAX*Y_BOUND_MAX) / (20000*scaleFactor));
        people = new Person[P_NUM];
        System.out.println(P_NUM);
        for(int i=0; i<P_NUM; i++){
            people[i] = new Person(scaleFactor);
            people[i].setXCoordinate(ThreadLocalRandom.current().nextDouble(0, X_BOUND_MAX));
            people[i].setYCoordinate(ThreadLocalRandom.current().nextDouble(0, Y_BOUND_MAX));
            Person Person = people[i];
            motionSpace.add(Person);  //people gat added to the JPanel that is for the motion of the people.
        }
        people[0].setInfected();
        people[1].setInfected();


        System.out.println("enter a decimal between 0 and 1. 0 is 0% and 1 is 100% infection rate");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextDouble()){ //this catches non-integer values
                System.out.println("Invalid input, please enter a decimal between 0 - 1.");
                commandLineInput.next();
            }
            userInputDouble=commandLineInput.nextDouble();
            if(userInputDouble<=1 && userInputDouble>0){
                loopAgain = false;
            }else {
                System.out.println("Invalid input, please enter a decimal between 0 - 1.");
            }
        }while(loopAgain);
        infectChance = userInputDouble;

        
        setDoubleBuffered(true);
        setLayout(null);
        totalInfectionsGraph = new Graph("Graph 1", "time(s)", "total infection amount", "total Infected", 0,0);
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
                        double infectDecider = ThreadLocalRandom.current().nextDouble(0, 1);
                        if(infectDecider < infectChance) {
                            infectee.setInfected();
                        }
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
        if(xPos> X_BOUND_MAX ){
            xAcc =+ -2*(1+xPos-X_BOUND_MAX);  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(xPos < 0){
            xAcc =+ -2*(-1+xPos);
        }
        if(yPos > Y_BOUND_MAX){
            yAcc =+ -2*(1+yPos-Y_BOUND_MAX);  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(yPos < 0){
            yAcc =+ -2*(-1+yPos);
        }

        //avoidance of other people//
        //yet to implement

        //main acceleration field//
        xAcc += (  Math.sin( X_BOUND_MAX*(xPos +X_BOUND_MAX/2d)-(yPos +Y_BOUND_MAX/2d) ) ); //2d to use floating point division
        yAcc += (  Math.cos( Y_BOUND_MAX*(xPos +X_BOUND_MAX/2d)+(yPos +Y_BOUND_MAX/2d) ) );

        //drag acting on people, slows them down. (restricts the growth of velocity)//
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
