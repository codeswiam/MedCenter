import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomePage extends JFrame {
    private WelcomePage welcomePage;
    public CardLayout cardLayout;
    public JPanel cardPanel;
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;

    public WelcomePage() {
        welcomePage = this;

        setTitle("Frankenstein Medical Center");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(welcomePage);
        signUpPanel = new SignUpPanel(welcomePage);

        cardPanel.add(loginPanel, "Login");
        cardPanel.add(signUpPanel, "Signup");

        add(cardPanel);

        loginPanel.getSignUpLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "Signup");
            }
        });

        signUpPanel.getLoginLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showLoginPanel();
            }
        });

        setVisible(true);
    }

    public void showLoginPanel() {
        cardLayout.show(cardPanel, "Login");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WelcomePage app = new WelcomePage();
                // app.setVisible(true);
            }
        });
    }
}

