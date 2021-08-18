import javax.swing.*;
import java.awt.*;

public class Window extends JFrame
{
    public Window()
    {
        int X_BOUND_MAX = 500;
        int Y_BOUND_MAX = 500;
        Simulation sim = new Simulation(X_BOUND_MAX, Y_BOUND_MAX);

        getContentPane().setPreferredSize(new Dimension(X_BOUND_MAX*2, Y_BOUND_MAX));
        getContentPane().setMinimumSize(new Dimension(640, 480));
        getContentPane().setMaximumSize(new Dimension(1920, 1080));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //add items
        setLayout(new BorderLayout());
        add(sim, BorderLayout.CENTER);  // the order that items are added will determine the rendering order

        pack();
        setVisible(true);
        toFront();
    }
}
