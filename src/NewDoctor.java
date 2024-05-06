import db.DBManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewDoctor extends JFrame {
    private JLabel loginLabel;
    private JTextField nameField;
    private JTextField departmentField;
    private JTextField telephoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    public final Connection connection = new DBManager().getConnection();
    public AdminDash adminDash;

    public NewDoctor(AdminDash adminDash) {
        this.adminDash = adminDash;

        setTitle("New Doctor");
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        setVisible(true);
    }

    public void initComponents() {
        // Input Fields
        JPanel inputPanel = new JPanel(new GridLayout(12, 1));
        this.setContentPane(inputPanel);
        inputPanel.setBackground(Colors.TROPICAL_BLUE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Full Name
        JLabel nameLabel = new customLabel("Full Name");
        inputPanel.add(nameLabel);

        nameField = new MyTextField();
        inputPanel.add(nameField);

        // Telephone
        JLabel telephoneLabel = new customLabel("Telephone");
        inputPanel.add(telephoneLabel);

        telephoneField = new MyTextField();
        inputPanel.add(telephoneField);

        // Department
        JLabel departmentLabel = new customLabel("Department");
        inputPanel.add(departmentLabel);

        departmentField = new MyTextField();
        inputPanel.add(departmentField);

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

        JButton saveDoctorButton = new JButton("Save Doctor");
        saveDoctorButton.setFont(new Font("Arial", Font.BOLD, 20));
        saveDoctorButton.setBackground(Colors.CORNFLOWER);
        saveDoctorButton.setOpaque(true);
        saveDoctorButton.setBorderPainted(false);
        saveDoctorButton.setFocusPainted(false);
        saveDoctorButton.setForeground(Color.white);

        saveDoctorButton.addActionListener(e -> {
            if (saveDoctor()) {
                dispose();
            }
        });

        inputPanel.add(saveDoctorButton);
    }

    public boolean saveDoctor() {
        String fullName = nameField.getText();
        String department = departmentField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();
        char[] password = passwordField.getPassword();

        // Check if any field is empty
        if (fullName.isEmpty() || department.isEmpty() ||
                telephone.isEmpty() || email.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(NewDoctor.this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (verifyFields(fullName, department, telephone, email, password)) {
            String insertPatientSQL = "INSERT INTO doctor (full_name, department, telephone, email, password) VALUES (?, ?, ?, ?, ?)";

            try {
                PreparedStatement statement = connection.prepareStatement(insertPatientSQL);

                statement.setString(1, fullName);
                statement.setString(2, department);
                statement.setString(3, telephone);
                statement.setString(4, email);
                statement.setString(5, String.valueOf(password));

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(NewDoctor.this, "Doctor was successfully registered.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    adminDash.refreshDoctorPanel();
                } else {
                    JOptionPane.showMessageDialog(NewDoctor.this, "Failed to register doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(NewDoctor.this, "Failed to register doctor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean verifyFields(String fullName, String department, String telephone, String email, char[] password) {
        String nameRegex = "[a-zA-Z ]+";
        // Check if full name contains only letters
        if (!fullName.matches(nameRegex)) {
            JOptionPane.showMessageDialog(NewDoctor.this, "Full name should only contain letters", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if department contains only letters
        if (!department.matches(nameRegex)) {
            JOptionPane.showMessageDialog(NewDoctor.this, "Department should only contain letters", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!telephone.matches("^\\d{10}$")) {
            JOptionPane.showMessageDialog(NewDoctor.this, "Invalid telephone number format", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(NewDoctor.this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if password has at least 6 characters
        if (password.length < 6) {
            JOptionPane.showMessageDialog(NewDoctor.this, "Password should be at least 6 characters long", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}

class customLabel extends JLabel {
    public customLabel(String string) {
        super(string);
        setForeground(Colors.BOSTON_BLUE);
        setFont(new Font("Arial", Font.BOLD, 20));
    }
}

class MyTextField extends JTextField {
    public MyTextField() {
        setFont(new Font("Arial", Font.BOLD, 20));
        setForeground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBackground(Colors.BOSTON_BLUE);
    }

    public MyTextField(int columns) {
        super(columns);
        setFont(new Font("Arial", Font.BOLD, 15));
        setForeground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBackground(Colors.BOSTON_BLUE);
    }
}

