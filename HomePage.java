import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame implements ActionListener
{
    static final int WIDTH = 1920;
    static final int LENGTH = 1080;
    public HomePage(BankAutomated BA, CA customer)
    {
        this.setTitle("Account Home");
        this.setLayout(null);



        this.getContentPane().setBackground(Color.white);
        this.setSize(WIDTH, LENGTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
