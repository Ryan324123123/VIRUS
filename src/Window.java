
/**
 * Write a description of class Windo here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import javax.swing.*;
import java.awt.*;

public class Window extends JFrame
{
    public Window()
    {
        Simulation sim = new Simulation();
        
        getContentPane().setPreferredSize(new Dimension(sim.getPerson(0).getXBound()+sim.getPerson(0).getRadius(), sim.getPerson(0).getYBound()+sim.getPerson(0).getRadius()));
        getContentPane().setMinimumSize(new Dimension(640, 480));
        getContentPane().setMaximumSize(new Dimension(1920, 1080));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //add itmes
        
        add(sim);  // the order that items are added will detimine the rendering order

        pack();
        toFront();
        setVisible(true);
    }
}
