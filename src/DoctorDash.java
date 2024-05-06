import javax.swing.*;
import java.awt.*;

public class DoctorDash extends JFrame {
    private String userEmail;

    public DoctorDash(String userEmail) {
        this.userEmail = userEmail;

        setTitle("Doctor Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, Dr. " + userEmail);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Add other components to the dashboard as needed

        add(panel);
    }
}
