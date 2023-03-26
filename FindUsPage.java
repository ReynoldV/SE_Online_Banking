import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.*;

public class FindUsPage extends JFrame implements ActionListener {
    static final int WIDTH = 1920;
    static final int LENGTH = 1080;
    private final JButton backToHome;
    BankAutomated Ba; 
    HomePage previous; 
    ArrayList<String> strts;
    public FindUsPage(BankAutomated BA, HomePage previous)
    {   
        this.setTitle("Our Locations");
        this.setLayout(null);
        this.Ba = BA; 
        this.previous = previous; 
        this.strts = Ba.addresses(); 



        Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
        backToHome = new JButton("Back to Home");
        backToHome.setFont(new Font("SansSerif", Font.PLAIN, 20));
        backToHome.setBounds(910, 900, 350, 50);
        backToHome.setBackground(Color.blue);
        backToHome.setForeground(Color.white);
        backToHome.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToHome.setBorder(border); 
        backToHome.addActionListener(this);
        this.add(backToHome); 
        

        for (int i = 0; i < strts.size(); i ++){
            JLabel street = new JLabel(strts.get(i)); 
            street.setForeground(Color.yellow);
            street.setFont(new Font("SansSerif", Font.PLAIN, (200/strts.size())));
            street.setBounds(700, 115 + (150 * i), 600, 200);
            this.add(street);
        }


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
        this.getContentPane().setBackground(Color.DARK_GRAY);
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
        try
        {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource("map.png")));
            g.drawImage(image, 1500, 200, 256,256, this);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
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
}