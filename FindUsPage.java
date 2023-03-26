import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.*;

public class FindUsPage extends JFrame implements ActionListener, MouseListener
{
    static final int WIDTH = 1920;
    static final int LENGTH = 1080;
    private final JButton backToHome;
    private JButton locationButton;
    private final JLabel locImage;
    private final JLabel locImage2;
    private final JLabel locImage3;
    BankAutomated BA;
    HomePage previous; 
    ArrayList<String> streets;

    public FindUsPage(BankAutomated BA, HomePage previous)
    {   
        this.setTitle("Our Locations");
        this.setLayout(null);

        this.BA = BA;
        this.previous = previous;
        this.streets = BA.addresses();
        Color bg = new Color(214, 215, 215);
        Border border = BorderFactory.createEmptyBorder();

        JLabel select = new JLabel("Select a location to see address:");
        select.setBorder(border);
        select.setFont(new Font("Raleway", Font.BOLD, 25));
        select.setBackground(bg);
        select.setForeground(Color.BLACK);
        select.setBounds(25, 125, 400, 40);
        this.add(select);

        backToHome = new JButton("Back to Home");
        backToHome.setFont(new Font("SansSerif", Font.PLAIN, 22));
        backToHome.setBounds(475, 600, 350, 50);
        backToHome.setBackground(bg);
        backToHome.setForeground(new Color(57, 107, 170));
        backToHome.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToHome.setContentAreaFilled(false);
        backToHome.setFocusPainted(false);
        backToHome.setBorder(border);
        backToHome.setContentAreaFilled(false);
        backToHome.addActionListener(this);
        this.add(backToHome); 

        for (int i = 0; i < streets.size(); i ++)
        {
            JLabel street = new JLabel(streets.get(i));
            street.setForeground(Color.yellow);
            street.setFont(new Font("SansSerif", Font.PLAIN, (200/streets.size())));
            street.setBounds(700, 115 + (150 * i), 600, 200);
            this.add(street);
        }

        ImageIcon pin = new ImageIcon(Objects.requireNonNull(getClass().getResource("mapPin.png")));
        locImage = new JLabel(pin);
        locImage.setBorder(border);
        locImage.setBounds(100,200,64,64);
        locImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        locImage.addMouseListener(this);
        this.add(locImage);

        locImage2 = new JLabel(pin);
        locImage2.setBorder(border);
        locImage2.setBounds(700,400,64,64);
        locImage2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        locImage2.addMouseListener(this);
        this.add(locImage2);

        locImage3 = new JLabel(pin);
        locImage3.setBorder(border);
        locImage3.setBounds(900,300,64,64);
        locImage3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        locImage3.addMouseListener(this);
        this.add(locImage3);

        this.addWindowListener(new WindowEventHandler() {
            @Override
            public void windowClosing(WindowEvent evt) {
                //BA.logout (logic.logout) would be called here
                //Write all changes to the file
                BA.logout();

                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    window.dispose();
                }

                System.exit(0);
            }
        });
        this.getContentPane().setBackground(new Color(255, 235, 235));
        this.getRootPane().setDefaultButton(backToHome);
        this.setSize(WIDTH, LENGTH);
        this.setVisible(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    public void paint(Graphics g)
    {
        
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        Color myRed = new Color(230, 30, 30);
        Color myBlack = new Color(160, 32, 32);
        GradientPaint redToBlack = new GradientPaint(0, 0, myRed, 0, 150, myBlack);
        g2.setPaint(redToBlack);
        g2.fillRect(0, 0, WIDTH+1, 150);

        Font regFont = new Font("Raleway", Font.BOLD, 60);
        g2.setFont(regFont);
        g2.setColor(new Color(250, 185, 60));
        g2.drawString("Our Current Locations ", 25, 110);
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == backToHome)
        {
            this.setVisible(false);
            previous.setVisible(true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getSource()==locImage)
        {
            JOptionPane.showMessageDialog(this, "The location you selected in 10153 King George Blvd, Vancouver, BC.");
        }
        else if (e.getSource()==locImage2)
        {
            JOptionPane.showMessageDialog(this, "The location you selected in 255 Yonge Street, Toronto, ON.");
        }
        else if (e.getSource()==locImage3)
        {
            JOptionPane.showMessageDialog(this, "The location you selected in 2210 Bank Street, Ottawa, ON.");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}