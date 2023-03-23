import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ForgotPage extends JFrame implements ActionListener
{
    static final int WIDTH = 1920;
    static final int LENGTH = 1080;
    LoginPage previous;
    BankAutomated BA;
    private final JTextField emailField;
    private final JButton requestReset;
    private final JButton backToLogin;

    public ForgotPage(BankAutomated logic, LoginPage login) {
        this.setTitle("Forgot Password");
        this.setLayout(null);
        previous = login;
        BA = logic;

        JLabel forgotPass = new JLabel("Forgot Password");
        forgotPass.setFont(new Font("Osward", Font.BOLD, 38));
        forgotPass.setBounds(835, 50, 350, 50);
        this.add(forgotPass);

        JLabel instructions = new JLabel("We'll email you instructions on");
        instructions.setForeground(new Color(100, 100, 100));
        instructions.setFont(new Font("Arial", Font.PLAIN, 20));
        instructions.setBounds(855, 120, 350, 30);
        this.add(instructions);

        JLabel instructions2 = new JLabel("how to reset it.");
        instructions2.setForeground(new Color(100, 100, 100));
        instructions2.setFont(new Font("Arial", Font.PLAIN, 20));
        instructions2.setBounds(917, 150, 350, 30);
        this.add(instructions2);

        JLabel enterEmail = new JLabel("Enter email address");
        enterEmail.setForeground(new Color(130, 130, 130));
        enterEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        enterEmail.setBounds(815, 300, 350, 30);
        this.add(enterEmail);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
        emailField = new JTextField(150);
        emailField.setBounds(815, 330, 350, 40);
        emailField.setFont(new Font("Arial", Font.PLAIN, 20));
        emailField.setBorder(border);
        this.add(emailField);

        requestReset = new JButton("Request Password Reset Link");
        requestReset.setFont(new Font("SansSerif", Font.PLAIN, 20));
        requestReset.setBounds(815, 450, 350, 50);
        requestReset.setBackground(Color.black);
        requestReset.setForeground(Color.white);
        requestReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        requestReset.addActionListener(this);
        this.add(requestReset);

        Border emptyBorder = BorderFactory.createEmptyBorder();
        backToLogin = new JButton("Back To Login");
        backToLogin.setFont(new Font("SansSerif", Font.PLAIN, 22));
        backToLogin.setBounds(815, 525, 350, 50);
        backToLogin.setBorder(emptyBorder);
        backToLogin.setBackground(Color.white);
        backToLogin.setForeground(new Color(57, 107, 170));
        backToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLogin.addActionListener(this);
        this.add(backToLogin);

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
        this.getContentPane().setBackground(Color.white);
        this.getRootPane().setDefaultButton(requestReset);
        this.setSize(WIDTH, LENGTH);
        this.setVisible(false);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }


    public void paint(Graphics g)
    {
        super.paint(g);

        try
        {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource("forgotPassIcon.png")));
            int w = image.getWidth();
            int h = image.getHeight();
            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            AffineTransform at = new AffineTransform();
            at.scale(0.5, 0.5);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            scaled = scaleOp.filter(image, scaled);
            g.drawImage(scaled, 25, 50, this);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == backToLogin)
        {
            this.setVisible(false);
            previous.setVisible(true);
        }
        else if (e.getSource() == requestReset)
        {
            String email = emailField.getText();
            if (BA.validEmail(email))
            {
                if (BA.existingEmail(email)) {
                    //pop up saying email has been sent
                    JOptionPane.showMessageDialog(this, "Follow instructions in the email sent" +
                            " to reset your password.\nYou will now be redirected to the login page.");
                    previous.setVisible(true);
                    previous.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    this.setVisible(false);
                }
                else {
                    //If an account with this email does not exist, show dialogue.
                    JOptionPane.showMessageDialog(this, "There is no existing account with this" +
                            " email.");
                    emailField.setText("");
                }
            }
            else
            {
                //pop up saying email is invalid
                JOptionPane.showMessageDialog(this, "Email format is invalid. Try again.");
                emailField.setText("");
            }
        }
    }
}