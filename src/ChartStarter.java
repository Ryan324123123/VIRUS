/**
import javax.swing.*;
public class ChartStarter {
    //THIS IS ALL JUST A STAND-IN FOR YOUR OWN CODE, THE PART YOU ACTUALLY WANT IS ChartExample.
    private double phase;
    private final Graph theChart;
    public static void main(String[] args) {
        new ChartStarter();
    }
    public ChartStarter() {
        double initialX = 0;
        double initialY = 0;
        theChart = new ChartExample("null", "null", "null", "null", initialX, initialY);
        Timer cycleTimer = new Timer(20, actionEvent -> doACycle());
        cycleTimer.setRepeats(true);
        cycleTimer.start();
    }
    private void doACycle() {
        double xPoint = phase + (2 * Math.PI / 1000);
        double yPoint = Math.sin(xPoint);
        phase += (2 * Math.PI * 2 / 20.0) * 0.1;
        //THE IMPORTANT PART
        theChart.addNewData(xPoint, yPoint);
    }
}
**/