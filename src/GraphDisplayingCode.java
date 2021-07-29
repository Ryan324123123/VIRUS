import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphDisplayingCode extends JPanel {
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

    public GraphDisplayingCode(String name, String XAxisTitle, String YAxisTitle) {
        graph=new XYChart(1,1);
        graph.setTitle(name);
        graph.setXAxisTitle(XAxisTitle);
        graph.setYAxisTitle(YAxisTitle);
        setLayout(new GridLayout(1,1));
        add(new XChartPanel<>(graph));
    }
    public void addSeries(double initX, double initY, String seriesName){
        arrayOfSeries.add(new DataSeries(initX, initY, seriesName));
    }
    public void addData(){


    }


}
