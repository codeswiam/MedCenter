import db.DBManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPanel extends JPanel {
    private JLabel signupLabel;
    private JRadioButton adminRadioButton;
    private JRadioButton doctorRadioButton;
    private JRadioButton patientRadioButton;

    private WelcomePage welcomePage;

    public final Connection connection = new DBManager().getConnection();

    public JLabel getSignUpLabel() {
        return signupLabel;
    }

    public LoginPanel(WelcomePage welcomePage) {

        this.welcomePage = welcomePage;

        setLayout(new BorderLayout());

        // Left panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Colors.TROPICAL_BLUE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(70, 50, 70, 50));

        // Logo and title
        JLabel logoLabel = new JLabel("Frankenstein Medical Center");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 30));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setForeground(Colors.MATISSE);
        leftPanel.add(logoLabel, BorderLayout.NORTH);

        // Input Fields
        JPanel inputPanel = new JPanel(new GridLayout(7, 1));
        inputPanel.setBackground(Colors.TROPICAL_BLUE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 5, 0));
        leftPanel.add(inputPanel, BorderLayout.CENTER);

        // roles radio buttons
        JPanel radioPanel = new JPanel(new GridLayout(1, 4));
        radioPanel.setBackground(Colors.TROPICAL_BLUE);
        inputPanel.add(radioPanel);

        JLabel rolesLabel = new MyLabel("Roles");
        radioPanel.add(rolesLabel);

        adminRadioButton = new MyRadioButton("Admin");
        adminRadioButton.setSelected(true);
        doctorRadioButton = new MyRadioButton("Doctor");
        patientRadioButton = new MyRadioButton("Patient");

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadioButton);
        roleGroup.add(doctorRadioButton);
        roleGroup.add(patientRadioButton);

        radioPanel.add(adminRadioButton);
        radioPanel.add(doctorRadioButton);
        radioPanel.add(patientRadioButton);

        // Email
        JLabel emailLabel = new MyLabel("Email");
        inputPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.BOLD, 20));
        emailField.setForeground(Color.white);
        emailField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        emailField.setBackground(Colors.BOSTON_BLUE);
        inputPanel.add(emailField);

        // Password
        JLabel pwdLabel = new MyLabel("Password");
        inputPanel.add(pwdLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setForeground(Color.white);
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        passwordField.setBackground(Colors.BOSTON_BLUE);
        inputPanel.add(passwordField);

        inputPanel.add(new JLabel());

        // Login button

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 20));
        loginButton.setBackground(Colors.CORNFLOWER);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setForeground(Color.white);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = "";
                if (adminRadioButton.isSelected()) {
                    role = "Admin";
                    System.out.println("Admin");
                } else if (doctorRadioButton.isSelected()) {
                    role = "Doctor";
                } else if (patientRadioButton.isSelected()) {
                    role = "Patient";
                }

                String email = emailField.getText();
                char[] password = passwordField.getPassword();

                // Authenticate user based on role
                if (!role.isEmpty() && authenticateUser(role, email, password)) {
                    showHomePage(email, role);
                } else {
                    // Display error message
                    JOptionPane.showMessageDialog(LoginPanel.this, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        inputPanel.add(loginButton);

        // Hyperlink
        signupLabel = new JLabel("Don't have an account? Sign Up.");
        signupLabel.setForeground(Colors.MATISSE);
        signupLabel.setHorizontalAlignment(SwingConstants.CENTER);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(signupLabel, BorderLayout.SOUTH);

        // Right panel (for the image)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.white);
        rightPanel.setLayout(new BorderLayout());
        try {
            // load the login image
            Image image = ImageIO.read(new File("/Users/wiam/uni/java/MedCenter/resources/images/login.png"));
            // scaling the image
            Image scaledImage = image.getScaledInstance(522, 466, Image.SCALE_SMOOTH);

            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            rightPanel.add(imageLabel, BorderLayout.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(rightPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Frankenstein Medical Center");
                frame.setSize(1100, 600);
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // frame.getContentPane().add(new LoginPanel());
                frame.setVisible(true);
            }
        });
    }

    private boolean authenticateUser(String role, String email, char[] password) {
        String query = "";

        if (role.equalsIgnoreCase("patient")) {
            query = "SELECT * FROM patient WHERE email = ? AND password = ?";
        } else if (role.equalsIgnoreCase("doctor")) {
            query = "SELECT * FROM doctor WHERE email = ? AND password = ?";
        } else {
            System.out.println("hello the role is admin");
            query = "SELECT * FROM admin WHERE email = ? AND password = ?";
        }

        try {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, email);
            statement.setString(2, String.valueOf(password));

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void showHomePage(String email, String role) {
        welcomePage.dispose();
        if (role.equalsIgnoreCase("patient")) {
            new PatientDash(email);
        } else if (role.equalsIgnoreCase("doctor")) {
            new DoctorDash(email);
        } else {
            new AdminDash(email);
        }
    }
}

class MyLabel extends JLabel {
    public MyLabel(String text) {
        super(text);
        setForeground(Colors.BOSTON_BLUE);
        setFont(new Font("Arial", Font.BOLD, 20));
    }
}

class MyRadioButton extends JRadioButton {
    public MyRadioButton(String text) {
        super(text);
        setForeground(Colors.MATISSE);
        setFont(new Font("Arial", Font.BOLD, 20));
    }
}


