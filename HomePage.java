import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame implements ActionListener
{
    static final int WIDTH = 1920;
    static final int LENGTH = 1080;
    private final String custName;
    private final JButton homeButton;
    private final JButton transferButton;
    private final JButton contactUSButton;
    private final JButton settingsButton;
    private final JButton findUsButton;
    private final JButton logoutButton;


    public HomePage(BankAutomated BA, CA customer)
    {
        this.setTitle("Account Home");
        this.setLayout(null);
        custName = customer.firstName;

        Font labels = new Font("Raleway", Font.BOLD, 25);
        Border emptyBorder = BorderFactory.createEmptyBorder();
        Color bg = new Color(240, 241, 241);
        //Color buttonColor = new Color(55, 110, 170);
        Color buttonColor = Color.BLACK;

        homeButton = new JButton("Home");
        homeButton.setFont(labels);
        homeButton.setBorder(emptyBorder);
        homeButton.setOpaque(false);
        homeButton.setFocusPainted(false);
        homeButton.setBackground(bg);
        homeButton.setForeground(buttonColor);
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeButton.setBounds(125, 125, 80, 40);
        homeButton.addActionListener(this);
        this.add(homeButton);

        transferButton = new JButton("Transfer");
        transferButton.setFont(labels);
        transferButton.setBorder(emptyBorder);
        transferButton.setOpaque(false);
        transferButton.setFocusPainted(false);
        transferButton.setBackground(bg);
        transferButton.setForeground(buttonColor);
        transferButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        transferButton.setBounds(280, 125, 120, 40);
        transferButton.addActionListener(this);
        this.add(transferButton);

        contactUSButton = new JButton("Contact Us");
        contactUSButton.setFont(labels);
        contactUSButton.setBorder(emptyBorder);
        contactUSButton.setOpaque(false);
        contactUSButton.setFocusPainted(false);
        contactUSButton.setBackground(bg);
        contactUSButton.setForeground(buttonColor);
        contactUSButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contactUSButton.setBounds(475, 125, 150, 40);
        contactUSButton.addActionListener(this);
        this.add(contactUSButton);

        settingsButton = new JButton("Settings");
        settingsButton.setFont(labels);
        settingsButton.setBorder(emptyBorder);
        settingsButton.setOpaque(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setBackground(bg);
        settingsButton.setForeground(buttonColor);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setBounds(685, 125, 120, 40);
        settingsButton.addActionListener(this);
        this.add(settingsButton);

        findUsButton = new JButton("Find Us");
        findUsButton.setFont(labels);
        findUsButton.setBorder(emptyBorder);
        findUsButton.setOpaque(false);
        findUsButton.setFocusPainted(false);
        findUsButton.setBackground(bg);
        findUsButton.setForeground(buttonColor);
        findUsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        findUsButton.setBounds(875, 125, 100, 40);
        findUsButton.addActionListener(this);
        this.add(findUsButton);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(labels);
        logoutButton.setBorder(emptyBorder);
        logoutButton.setOpaque(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(bg);
        logoutButton.setForeground(buttonColor);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setBounds(1050, 125, 100, 40);
        logoutButton.addActionListener(this);
        this.add(logoutButton);


        //White rectangle with Chequing/Savings accounts and their funds to the right-hand side. The name of the account
        //is clickable. Set rectangle as a JButton that doesn't press with setAreaFilled(false) and setOpaque(false)

        this.getContentPane().setBackground(bg);
        this.setSize(WIDTH, LENGTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
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
        g2.drawString("Welcome Back, " + custName, 25, 110);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
