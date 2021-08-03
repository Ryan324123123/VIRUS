import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphManager extends JPanel {
    private final XYChart graph;
    private final ArrayList<DataSeries> arrayOfSeries = new ArrayList<>();

    private static class DataSeries {
        private final ArrayList<Double> xData = new ArrayList<>();
        private final ArrayList<Double> yData = new ArrayList<>();
        private final String nameOfSeries;
        public DataSeries(double initX, double initY, String nameOfSeries){
            xData.add(initX);
            yData.add(initY);
            this.nameOfSeries = nameOfSeries;
        }
        public void addDataPoint(double newXDataPoint, double newYDataPoint){
            xData.add(newXDataPoint);
            yData.add(newYDataPoint);
        }
    }

    public GraphManager(String title, String XAxisTitle, String YAxisTitle) {
        graph=new XYChart(1,1);
        graph.setTitle(title);
        graph.setXAxisTitle(XAxisTitle);
        graph.setYAxisTitle(YAxisTitle);
        setLayout(new GridLayout(1,1));
        add(new XChartPanel<>(graph));
    }
    public void addSeries(double initX, double initY, String seriesName){
        arrayOfSeries.add(new DataSeries(initX, initY, seriesName));
        graph.addSeries(seriesName, new double[]{initX}, new double[]{initY});
    }
    public void addData(String seriesName, double nextX, double nextY ){
        for (DataSeries dataSeries : arrayOfSeries) { //enhanced for loop for nicer code, dataSeries is the identifier for the particular one we are looking at, at any moment.
            if (dataSeries.nameOfSeries.equals(seriesName)) {
                dataSeries.addDataPoint(nextX, nextY);
                graph.updateXYSeries(seriesName, dataSeries.xData, dataSeries.yData, null);
                break; //once the match has been made, don't check the others.
            } else {
                System.out.println("impossible condition reached. Oh god make it end.");
            }
        }
    }


}
