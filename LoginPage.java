import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class LoginPage extends JFrame implements ActionListener
{
    static final int WIDTH = 1920;
    static final int LENGTH = 1080;
    private final JButton loginButton;
    private final JButton clearButton;
    private final JButton registerButton;
    private final JButton forgotButton;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JCheckBox showPassword;

    BankAutomated logic;

    CA customer;

    public LoginPage(BankAutomated BA)
    {
        this.setTitle("Welcome! Login to Continue...");
        this.setLayout(null);

        logic = BA;

        JLabel welcome = new JLabel("Welcome to The");
        welcome.setFont(new Font("Osward", Font.PLAIN, 38));
        welcome.setBounds(825, 50, 350, 40);
        this.add(welcome);

        JLabel bcs = new JLabel("Bank Of Computer Science");
        bcs.setFont(new Font("Imprint MT Shadow", Font.BOLD, 38));
        bcs.setForeground(new Color(246, 160, 62));
        bcs.setBounds(720, 100, 500, 40);
        this.add(bcs);

        JLabel email = new JLabel("Email: ");
        email.setFont(new Font("Osward", Font.PLAIN, 28));
        email.setBounds(720, 250, 150, 30);
        this.add(email);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
        emailField = new JTextField(150);
        emailField.setBounds(875, 250, 350, 30);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        emailField.setBorder(border);
        this.add(emailField);

        JLabel password = new JLabel("Password: ");
        password.setFont(new Font("Osward", Font.PLAIN, 28));
        password.setBounds(720, 350, 150, 30);
        this.add(password);

        passwordField = new JPasswordField(100);
        passwordField.setBounds(875, 350, 350, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(border);
        this.add(passwordField);

        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(875, 380, 200, 30);
        showPassword.setBackground(Color.white);
        showPassword.setForeground(Color.black);
        showPassword.addActionListener(this);
        this.add(showPassword);

        loginButton = new JButton("SIGN IN");
        loginButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        loginButton.setBounds(825, 450, 120, 50);
        loginButton.setBackground(Color.black);
        loginButton.setForeground(Color.white);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this);
        this.add(loginButton);

        clearButton = new JButton("CLEAR");
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        clearButton.setBounds(1000, 450, 120, 50);
        clearButton.setBackground(Color.black);
        clearButton.setForeground(Color.white);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(this);
        this.add(clearButton);

        registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        registerButton.setBounds(825, 525, 295, 50);
        registerButton.setBackground(Color.black);
        registerButton.setForeground(Color.white);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(this);
        this.add(registerButton);

        Border emptyBorder = BorderFactory.createEmptyBorder();
        forgotButton = new JButton("Forgot Password?");
        forgotButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        forgotButton.setBounds(1135, 380, 90, 30);
        forgotButton.setBorder(emptyBorder);
        forgotButton.setBackground(Color.white);
        forgotButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotButton.setHorizontalAlignment(SwingConstants.RIGHT);
        forgotButton.addActionListener(this);
        this.add(forgotButton);

        this.addWindowListener(new WindowEventHandler() {
            @Override
            public void windowClosing(WindowEvent evt) {
                //BA.logout (logic.logout) would be called here
                //Write all changes to the file
                logic.logout();
                
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    window.dispose();
                }
        
                System.exit(0);
            }
        });
        this.setSize(WIDTH, LENGTH);
        this.getContentPane().setBackground(Color.white);
        this.getRootPane().setDefaultButton(loginButton);
        this.setVisible(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }


    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        Color myRed = new Color(232, 30, 30);
        Color myBlack = new Color(131, 26, 26);
        GradientPaint redToBlack = new GradientPaint(0, 0, myRed, 0, 750, myBlack);
        g2.setPaint(redToBlack);
        g2.fillRect(0, 0, 650, LENGTH+5);

        try
        {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource("BCS_logo.png")));
            g.drawImage(image, -25, 175, this);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (showPassword.isSelected())
        {
            passwordField.setEchoChar('\u0000');
        }
        else
        {
            passwordField.setEchoChar('*');
        }

        if (e.getSource() == clearButton)
        {
            emailField.setText("");
            passwordField.setText("");
            showPassword.setSelected(false);
        }
        else if (e.getSource() == loginButton)
        {
            //Call the BA.login (logic.login in this case)
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            if (email.equals("") || password.equals(""))
            {
                JOptionPane.showMessageDialog(this, "Email/Password cannot be blank.");
                emailField.setText("");
                passwordField.setText("");
                showPassword.setSelected(false);
                return;
            }

            //Check validity of email and whether is exists
            if (logic.validEmail(email) && !logic.existingEmail(email))
            {
                JOptionPane.showMessageDialog(this, "Email is not registered with an " +
                        "account.\nSelect Register to create an account with us.");
                emailField.setText("");
                passwordField.setText("");
                showPassword.setSelected(false);
                return;
            }

            customer = logic.loginAccount(email, password);
            
            // Do other thing
            if (customer != null)
            {
                // Debug System.out.println("Success");
                //Show homepage for the customer
                HomePage home = new HomePage(this, logic, customer);
                emailField.setText("");
                passwordField.setText("");
                showPassword.setSelected(false);
                this.setVisible(false);
                home.setVisible(true);
            }
            else
            {
                // Debug System.out.println("Incorrect");
                JOptionPane.showMessageDialog(this, "Email/Password invalid. Try again.");
                emailField.setText("");
                passwordField.setText("");
                showPassword.setSelected(false);
            }
        }
        else if (e.getSource() == registerButton)
        {
            RegisterPage register = new RegisterPage(logic, this);
            emailField.setText("");
            passwordField.setText("");
            showPassword.setSelected(false);
            this.setVisible(false);
            register.setVisible(true);
        }
        else if (e.getSource() == forgotButton)
        {
            emailField.setText("");
            passwordField.setText("");
            showPassword.setSelected(false);
            ForgotPage forgot = new ForgotPage(logic, this);
            this.setVisible(false);
            forgot.setVisible(true);
        }
    }
}

class WindowEventHandler extends WindowAdapter {
    public void windowClosing(WindowEvent evt) {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            window.dispose();
        }

        System.exit(0);
    }
}
