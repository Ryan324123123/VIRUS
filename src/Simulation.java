import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

public class Simulation extends JPanel
{
    private int cycleCount=0;
    private final Person[] people;
    private final GraphManager virusProperties;
    private final GraphManager worldTallies;
    private final int P_NUM;
    private final int X_BOUND_MAX;
    private final int Y_BOUND_MAX;
    private final double infectChance;
    private final double virusMutationRate;
    private final static int FRAME_TIME = 30;  //processing can take from 0 - 25 ms ish depending on some parameters
    private final static String seriesRecovered = "Tally of Recovered People"; //these are the names of each data series which is called a few times in different methods so they must be given as exact Strings hence using a static variable
    private final static String seriesInfected = "Tally of Infected People";
    private final static String seriesVirusPotency = "Average potency of SLAP21";
    private final static String seriesPersonResistivity = "Average Resistivity of people";

    public Simulation(int X_BOUND, int Y_BOUND) {
        this.X_BOUND_MAX = X_BOUND; //I use X_BOUND_MAX in all the code so to keep things consistent ill just use *_MAX
        this.Y_BOUND_MAX = Y_BOUND;
        setDoubleBuffered(true);
        setBackground(new Color(0, 0, 0));
        setLayout(new GridLayout(1,2,2,0));
        //motion space is redundant in this iteration of hte program but for future use I have put the visual representation into its own jPanel
        JPanel motionSpace = new JPanel();
        motionSpace.setLayout(null);
        add(motionSpace);
        JPanel graphSpace = new JPanel();
        graphSpace.setLayout(new GridLayout(2, 1)); //indents to show that the two graphs get added to graph space and then graphSpace to simulation
            virusProperties = new GraphManager("Virus Properties", "Time", "Properties" );
            graphSpace.add(virusProperties);
            worldTallies = new GraphManager("Tallies for the simulation", "Time", "Various Tallies");
            graphSpace.add(worldTallies);
        add(graphSpace);

        Scanner commandLineInput = new Scanner(System.in);
        //these three variables are used for userInput checking
        int userInput; //userInput is always an int by design
        boolean loopAgain;

        //by getting the area they wish to simulate I can then scale the people to fit so they are the correct corresponding size/scale AND I can scale the amount of people to match the population density.
        System.out.println("Enter the open space area that you wish to simulate (250,000 is normal)");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextInt()){ //this catches non-integer values
                System.out.println("Invalid input, please enter an integer around the normal value (150,000m^2 - 500,000m^2)");
                commandLineInput.next();
            }
            userInput=commandLineInput.nextInt();
            if(userInput>=150000 && userInput <= 500000){
                loopAgain = false;
            }else{
                System.out.println("Invalid input, please enter an integer around the normal value (150,000m^2 - 500,000m^2)");
            }
        }while(loopAgain);
        double scaleFactor = X_BOUND_MAX*Y_BOUND_MAX*1.0/ (1.0*userInput); // is the right type of division
        System.out.println(scaleFactor); //todo: THIS IS A DEBUG PRINT

        //get a value for how fast the virus can mutate
        System.out.println("Enter from 1 to 10 how fast the virus mutates between each infection.");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextInt()){ //this catches non-integer values
                System.out.println("Invalid input, please enter an integer between 1 - 10.");
                commandLineInput.next();
            }
            userInput=commandLineInput.nextInt();
            if(userInput<=10 && userInput>0){
                loopAgain = false;
            }else{
                System.out.println("Invalid input, please enter an integer between 1 - 10.");
            }
        }while(loopAgain);
        virusMutationRate = userInput/5.0; //the virus can mutate much faster than people.

        //by using population density, calculate the amount of people to simulate for.
        //while the userInput is not in the 1-10 range, keep asking for the input.
        System.out.println("Enter the population density you want (1 is the least, 10 is the max.)");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextInt()){ //this catches non-integer values
                System.out.println("Invalid input, please enter an integer between 1 - 10.");
                commandLineInput.next();
            }
            userInput=commandLineInput.nextInt();
            if(userInput<=10 && userInput>0){
                loopAgain = false;
            }else{
                System.out.println("Invalid input, please enter an integer between 1 - 6.");
            }
        }while(loopAgain);
        P_NUM = (int) ((15*X_BOUND_MAX*Y_BOUND_MAX * userInput) / (200000*scaleFactor));


        people = new Person[P_NUM];
        System.out.println(P_NUM);
        for(int i=0; i<P_NUM; i++){
            people[i] = new Person(scaleFactor);
            people[i].setXCoordinate(ThreadLocalRandom.current().nextDouble(0, X_BOUND_MAX));
            people[i].setYCoordinate(ThreadLocalRandom.current().nextDouble(0, Y_BOUND_MAX));
            Person Person = people[i];
            motionSpace.add(Person);  //people gat added to the JPanel that is for the motion of the people.
        }
        people[0].setInfected(1.0, virusMutationRate); // virus potency starts at 1
        people[1].setInfected(1.0, virusMutationRate);


        System.out.println("enter an integer between 0 and 10 for the infection chance, the range is 0% to 100%. Most virus have an infect chance of 8 - 10");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextInt()){ //this catches non-integer values
                System.out.println("Invalid input, please enter a integer between 0 - 10.");
                commandLineInput.next();
            }
            userInput=commandLineInput.nextInt();
            if(userInput<=10 && userInput>0){
                loopAgain = false;
            }else {
                System.out.println("Invalid input, please enter a integer between 0 - 10.");
            }
        }while(loopAgain);
        infectChance = userInput/10.0;

        virusProperties.addSeries(0,0,seriesVirusPotency);
        virusProperties.addSeries(0,0, seriesPersonResistivity);
        worldTallies.addSeries(0,P_NUM,seriesRecovered);
        worldTallies.addSeries(0,0,seriesInfected);

        Timer cycleTimer = new Timer(FRAME_TIME, actionEvent -> SwingUtilities.invokeLater(this::updatePeople));
        cycleTimer.setRepeats(true);
        cycleTimer.start();
    }


    private void updatePeople() {
        int tallyInfectedPeople = 0;
        int tallyHealthyPeople = P_NUM;
        double avgVirusPotency = 1;
        double avgPersonResistivity = 1;

        for(int i=0; i<P_NUM; i++){
            people[i].infectionEvolution();
            avgPersonResistivity += people[i].getVirusImmunity()/P_NUM;
            if(people[i].getInfectionLevel() > 1){
                tallyInfectedPeople += 1;
                tallyHealthyPeople -= 1;
                avgVirusPotency += people[i].getVirusPotency()/P_NUM;
            }
            movePerson(i);
            Collide(i);
        }

        cycleCount ++;
        worldTallies.addData(seriesInfected,cycleCount,tallyInfectedPeople);
        worldTallies.addData(seriesRecovered,cycleCount,tallyHealthyPeople);
        virusProperties.addData(seriesVirusPotency,cycleCount,Math.log(avgVirusPotency));
        virusProperties.addData(seriesPersonResistivity,cycleCount,Math.log(avgPersonResistivity));
        repaint();
        System.out.println(Math.log(avgVirusPotency)/Math.log(avgPersonResistivity));
    }

    private void Collide(int i) {
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
                    if(distance <= people[0].getDiameter()*1.1){
                        if(infectee.getVirusImmunity() < infector.getVirusPotency()){
                            double infectDecider = ThreadLocalRandom.current().nextDouble(0, 1);
                            if(infectDecider < infectChance) {
                                infectee.setInfected(infector.getVirusPotency(), virusMutationRate); //infectee is infected with infector's virus
                            }
                        }
                    }
                }
            }
        }
    private void movePerson(int i) {
        //get the pos and vel of the person this method is moving
        double xVel = people[i].getXVelocity();
        double yVel = people[i].getYVelocity();
        //Person movement Variables
        double xPos = people[i].getXCoordinate();
        double yPos = people[i].getYCoordinate();

        double xAcc = 0;
        double yAcc = 0;

        //if statements for wall boundaries //
        if(xPos> X_BOUND_MAX-people[i].getDiameter()){
            xAcc =+ -2*(1+xPos-X_BOUND_MAX+people[i].getDiameter());  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(xPos < 0){
            xAcc =+ -2*(-1+xPos);
        }
        if(yPos > Y_BOUND_MAX-people[i].getDiameter()){
            yAcc =+ -2*(1+yPos-Y_BOUND_MAX+people[i].getDiameter());  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(yPos < 0){
            yAcc =+ -2*(-1+yPos);
        }

        //main acceleration field//
        xAcc += (  Math.sin( X_BOUND_MAX*(xPos +X_BOUND_MAX/2d)-(yPos +Y_BOUND_MAX/2d) ) ); //'d' to use floating point division
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

        people[i].setLocation((int) xPos, (int) yPos);  //this changes the graphical 'location' with reference to the jPanel
    }
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
    }
}