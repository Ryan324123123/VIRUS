import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

public class Simulation extends JPanel
{
    private final Person[] people;
    private final int P_NUM;
    private static final int FRAME_TIME = 30;  //processing can take from 0 - 25 ms ish depending on some parameters
    private final GraphManager virusProperties;
    private final GraphManager worldTallies;
    private int cycleCount=0;
    private final int X_BOUND_MAX;  //todo: next step is to have this work for any given window its put in. (adds resize compatibility)
    private final int Y_BOUND_MAX;  //because if the window that the motionSpace goes into is smaller that the XY bounds then the people move off the screen
    private final double infectChance;
    private final static String seriesRecovered = "Tally of Recovered People";
    private final static String seriesInfected = "Tally of Infected People";
    private final static String seriesInfectionLevel = "Total Infectivity for all people";
    private final JPanel motionSpace; //motion space is redundant in this iteration of hte program but for future use I have put the visual representation into its own jPanel
    GridBagConstraints constraints = new GridBagConstraints();


    public Simulation(int X_BOUND, int Y_BOUND)
    {
        this.X_BOUND_MAX = X_BOUND; //I use X_BOUND_MAX in all the code so to keep things consistent ill just use *_MAX
        this.Y_BOUND_MAX = Y_BOUND;
        setDoubleBuffered(true);
        setBackground(new Color(0, 0, 0));
        setLayout(new GridLayout(1,2,2,0));
        motionSpace = new JPanel();
        motionSpace.setLayout(null);
        add(motionSpace);
        JPanel graphSpace = new JPanel();
        graphSpace.setLayout(new GridLayout(2, 1)); //indents to show that the two graphs get added to graph space and then graphSpace to simulation
            virusProperties = new GraphManager("Current Infectivity of all People", "Time", "Infectivity" );
            graphSpace.add(virusProperties);
            worldTallies = new GraphManager("Tallies for the simulation", "Time", "Various Tallies");
            graphSpace.add(worldTallies);
        add(graphSpace);

        Scanner commandLineInput = new Scanner(System.in);
        //these three variables are used for userInput checking
        int userInputInt;
        double userInputDouble;
        boolean loopAgain;

        //by getting the area they wish to simulate I can then scale the people to fit so they are the correct corresponding size/scale.
        System.out.println("Enter the open space area that you wish to simulate (250,000 is normal)");
        do {
            loopAgain=true;
            while(!commandLineInput.hasNextInt()){ //this catches non-integer values
                System.out.println("Invalid input, please enter an integer around the normal value (150,000m^2 - 500,000m^2)");
                commandLineInput.next();
            }
            userInputInt=commandLineInput.nextInt();
            if(userInputInt>=150000 && userInputInt <= 500000){
                loopAgain = false;
            }else{
                System.out.println("Invalid input, please enter an integer around the normal value (150,000m^2 - 500,000m^2)");
            }
        }while(loopAgain);
        double scaleFactor = X_BOUND_MAX*Y_BOUND_MAX*1.0/ (1.0*userInputInt); // is the right type of division      //todo fix equatioun
        System.out.println(scaleFactor);
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
            if(userInputInt<=6 && userInputInt>0){
                loopAgain = false;
            }else{
                System.out.println("Invalid input, please enter an integer between 1 - 6.");
            }
        }while(loopAgain);
        P_NUM = (int)  (X_BOUND_MAX*Y_BOUND_MAX / (userInputInt*scaleFactor));  //todo fix equationj


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


        System.out.println("enter a decimal between 0 and 1. 0 is 0% and 1 is 100% infection chance");
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

        virusProperties.addSeries(0,0,seriesInfectionLevel);
        worldTallies.addSeries(0,P_NUM,seriesRecovered);
        worldTallies.addSeries(0,0,seriesInfected);

        Timer cycleTimer = new Timer(FRAME_TIME, actionEvent -> SwingUtilities.invokeLater(this::updatePeople));
        cycleTimer.setRepeats(true);
        cycleTimer.start();
    }


    private void updatePeople()
    {
        int tallyInfectedPeople = 0;
        int tallyHealthyPeople = P_NUM;
        for(int i=0; i<P_NUM; i++){
            people[i].infectionEvolution();
            if(people[i].getInfectionLevel() > 1){
                tallyInfectedPeople += 1;
                tallyHealthyPeople -= 1;
            }
            movePerson(i);
        }
        Collide();
        cycleCount ++;

        worldTallies.addData(seriesInfected,cycleCount,tallyInfectedPeople);
        worldTallies.addData(seriesRecovered,cycleCount,tallyHealthyPeople);
        //infectionGraph.addData(seriesInfectionLevel,cycleCount,totalVirus);  //add back in as graph of virus infectivities
        repaint();
    }


    private void Collide()
    {
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
        if(xPos> X_BOUND_MAX-people[i].getRadius()){
            xAcc =+ -2*(1+xPos-X_BOUND_MAX+people[i].getRadius());  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(xPos < 0){
            xAcc =+ -2*(-1+xPos);
        }
        if(yPos > Y_BOUND_MAX-people[i].getRadius()){
            yAcc =+ -2*(1+yPos-Y_BOUND_MAX+people[i].getRadius());  //the acceleration due to being out of bounds is a function of how far out it is.
        }else if(yPos < 0){
            yAcc =+ -2*(-1+yPos);
        }

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

        people[i].setLocation((int) xPos, (int) yPos);  //this changes the graphical 'location' with reference to the jPanel
    }
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
    }

}