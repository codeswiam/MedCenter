import db.DBManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpPanel extends JPanel {
    private JLabel loginLabel;
    private JTextField nameField;
    private JTextField dayField;
    private JTextField monthField;
    private JTextField yearField;
    private JTextField telephoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    public final Connection connection = new DBManager().getConnection();
    private WelcomePage welcomePage;

    public JLabel getLoginLabel() {
        return loginLabel;
    }

    public SignUpPanel(WelcomePage welcomePage) {
        this.welcomePage = welcomePage;

        setLayout(new BorderLayout());

        // Left panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Colors.TROPICAL_BLUE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Logo and title
        JLabel logoLabel = new JLabel("Frankenstein Medical Center");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 30));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setForeground(Colors.MATISSE);
        leftPanel.add(logoLabel, BorderLayout.NORTH);

        // Input Fields
        JPanel inputPanel = new JPanel(new GridLayout(12, 1));
        inputPanel.setBackground(Colors.TROPICAL_BLUE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        leftPanel.add(inputPanel, BorderLayout.CENTER);

        // Full Name
        JLabel nameLabel = new customLabel("Full Name");
        inputPanel.add(nameLabel);

        nameField = new MyTextField();
        inputPanel.add(nameField);

        // Birthdate
        JLabel dateLabel = new customLabel("Birthdate (DD/MM/YYYY)");
        inputPanel.add(dateLabel);

        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        datePanel.setBackground(Colors.TROPICAL_BLUE);
        inputPanel.add(datePanel);

        // Create day text field
        dayField = new MyTextField(2);
        datePanel.add(dayField);
        datePanel.add(new customLabel("   /   "));

        // Create month text field
        monthField = new MyTextField(2);
        datePanel.add(monthField);
        datePanel.add(new customLabel("   /   "));

        // Create year text field
        yearField = new MyTextField(4);
        datePanel.add(yearField);

        // Telephone
        JLabel telephoneLabel = new customLabel("Telephone");
        inputPanel.add(telephoneLabel);

        telephoneField = new MyTextField();
        inputPanel.add(telephoneField);

        // Email
        JLabel emailLabel = new customLabel("Email");
        inputPanel.add(emailLabel);

        emailField = new MyTextField();
        inputPanel.add(emailField);

        // Password
        JLabel pwdLabel = new customLabel("Password");
        inputPanel.add(pwdLabel);

        passwordField = new JPasswordField();
        passwordField.setForeground(Color.white);
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        passwordField.setBackground(Colors.BOSTON_BLUE);
        inputPanel.add(passwordField);

        inputPanel.add(new JLabel());

        // Sign Up button

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 20));
        signUpButton.setBackground(Colors.CORNFLOWER);
        signUpButton.setOpaque(true);
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setForeground(Color.white);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (saveUser()) {
                    welcomePage.showLoginPanel();
                }
            }
        });

        inputPanel.add(signUpButton);

        // Hyperlink
        loginLabel = new JLabel("Already have an account? Log In.");
        loginLabel.setForeground(Colors.MATISSE);
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(loginLabel, BorderLayout.SOUTH);

        // Right panel (for the image)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.white);
        rightPanel.setLayout(new BorderLayout());
        try {
            // load the login image
            Image image = ImageIO.read(new File("/Users/wiam/uni/java/MedCenter/resources/images/signup.png"));
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
                // frame.getContentPane().add(new SignUpPanel());
                frame.setVisible(true);
            }
        });
    }

    public boolean saveUser() {
        String fullName = nameField.getText();
        String day = dayField.getText();
        String month = monthField.getText();
        String year = yearField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();
        char[] password = passwordField.getPassword();

        // Check if any field is empty
        if (fullName.isEmpty() || day.isEmpty() || month.isEmpty() || year.isEmpty() ||
                telephone.isEmpty() || email.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(SignUpPanel.this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String date = day + "/" + month + "/" + year;

        if (verifyFields(fullName, date, telephone, email, password)) {
            String insertPatientSQL = "INSERT INTO patient (full_name, birth_date, telephone, email, password) VALUES (?, ?, ?, ?, ?)";

            try {
                PreparedStatement statement = connection.prepareStatement(insertPatientSQL);

                Date birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(date); // turning the string it into a date
                java.sql.Date birthDateSql = new java.sql.Date(birthDate.getTime()); // turning the date into an sql approved date

                statement.setString(1, fullName);
                statement.setDate(2, birthDateSql);
                statement.setString(3, telephone);
                statement.setString(4, email);
                statement.setString(5, String.valueOf(password));

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(SignUpPanel.this, "Registration successful. Log in with the new credentials.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(SignUpPanel.this, "Failed to register user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(SignUpPanel.this, "Failed to register user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean verifyFields(String fullName, String dateString, String telephone, String email, char[] password) {

        // Check if full name contains only letters
        if (!fullName.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(SignUpPanel.this, "Full name should only contain letters", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // enforcing strict parsing

        try {
            Date date = sdf.parse(dateString);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(SignUpPanel.this, "Invalid date format", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate telephone number
        Pattern phonePattern = Pattern.compile("^\\d{10}$");
        Matcher phoneMatcher = phonePattern.matcher(telephone);

        if (!phoneMatcher.matches()) {
            JOptionPane.showMessageDialog(SignUpPanel.this, "Invalid telephone number format", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(email);

        if (!emailMatcher.matches()) {
            JOptionPane.showMessageDialog(SignUpPanel.this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        // Check if password has at least 6 characters
        if (password.length < 6) {
            JOptionPane.showMessageDialog(SignUpPanel.this, "Password should be at least 6 characters long", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}

class Label extends JLabel {
    public Label(String string) {
        super(string);
        setForeground(Colors.BOSTON_BLUE);
        setFont(new Font("Arial", Font.BOLD, 20));
    }
}

class TextField extends JTextField {
    public TextField() {
        setFont(new Font("Arial", Font.BOLD, 20));
        setForeground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBackground(Colors.BOSTON_BLUE);
    }

    public TextField(int columns) {
        super(columns);
        setFont(new Font("Arial", Font.BOLD, 15));
        setForeground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBackground(Colors.BOSTON_BLUE);
    }
}
