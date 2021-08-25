import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphManager extends JPanel {
    private final XYChart graph;
    private final ArrayList<DataSeries> arrayOfSeries = new ArrayList<>();

    private static class DataSeries {  //make a class that acts as a data structure, it holds two array lists that hold the data coordinates to plot for a series
        private final ArrayList<Double> xData = new ArrayList<>();
        private final ArrayList<Double> yData = new ArrayList<>();
        private final String nameOfSeries;
        public DataSeries(double initX, double initY, String nameOfSeries){
            xData.add(initX);
            yData.add(initY);
            this.nameOfSeries = nameOfSeries;
        }
        public void addDataPoint(double newXDataPoint, double newYDataPoint){ //this is the method to add data within a given dataseries
            xData.add(newXDataPoint);
            yData.add(newYDataPoint);
        }
    }

    public GraphManager(String title, String XAxisTitle, String YAxisTitle) {  //this is the design of the graphs that I want, any changes to the graph should go in here
        graph=new XYChart(1,1);
        graph.setTitle(title);
        graph.setXAxisTitle(XAxisTitle);  //the purpose of these methods is in the names
        graph.setYAxisTitle(YAxisTitle);
        setLayout(new GridLayout(1,1));
        add(new XChartPanel<>(graph));  //because the graph is a visual element it gets added to the JPanel(GraphManager extends JPanel)
    }
    public void addSeries(double initX, double initY, String seriesName){ //to add a series, the series is added to the list of added series, this makes management of them easy
        arrayOfSeries.add(new DataSeries(initX, initY, seriesName));
        graph.addSeries(seriesName, new double[]{initX}, new double[]{initY}); //then the data point is added to the instance of the sub class defined above
    }
    public void addData(String seriesName, double nextX, double nextY ){  //to add data I get given the graph and i find the series from the list of indexed ones and add the datapoint to the array list
        Integer matchIndex=null;
        for (int i =0; i<arrayOfSeries.size(); i++) {
            if (arrayOfSeries.get(i).nameOfSeries.equals(seriesName)) {
                matchIndex = i;
                break; //once the match has been made, don't check the others.
            }
        }

        if(matchIndex != null){ //if there is no match found an error has ocured, becaues I use variable names in the code that runs this, this should never happen
            arrayOfSeries.get(matchIndex).addDataPoint(nextX, nextY);
            graph.updateXYSeries(seriesName, arrayOfSeries.get(matchIndex).xData, arrayOfSeries.get(matchIndex).yData, null);
        }else {
            System.out.println("impossible condition reached. Oh god make it end.");
            System.out.println("caused by: " + seriesName + ", Series on Graph:");  //this is just helpfull error messaging
            for (DataSeries seriesToPrint : arrayOfSeries) {
                System.out.print(seriesToPrint.nameOfSeries + ", ");
            }
            System.out.println();
        }
    }
}