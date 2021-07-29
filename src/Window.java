import javax.swing.*;
import java.awt.*;

public class Window extends JFrame
{
    public Window()
    {

        Simulation sim = new Simulation();
        
        getContentPane().setPreferredSize(new Dimension((int)(sim.getX_BOUND_MAX()+sim.getPerson(0).getRadius()), (int) (sim.getY_BOUND_MAX()+sim.getPerson(0).getRadius())));
        getContentPane().setMinimumSize(new Dimension(640, 480));
        getContentPane().setMaximumSize(new Dimension(1920, 1080));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //add items
        add(sim);  // the order that items are added will determine the rendering order

        pack();
        toFront();
        setVisible(true);
    }
}
