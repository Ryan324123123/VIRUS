import javax.swing.*;
import java.awt.*;

public class Window extends JFrame
{
    public Window()
    {
        int X_BOUND_MAX = 500;//set bounds
        int Y_BOUND_MAX = 500;
        Simulation sim = new Simulation(X_BOUND_MAX, Y_BOUND_MAX); //make a simulation

        getContentPane().setPreferredSize(new Dimension(X_BOUND_MAX*2, Y_BOUND_MAX)); // min or max size not needed, this sets the size of the GUI window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //add items
        setLayout(new BorderLayout());
        add(sim, BorderLayout.CENTER);  // this adds the sim to the list of things to print on repaint

        pack();
        setVisible(true);//set visable and send to front for convenience
        toFront();
    }
}
