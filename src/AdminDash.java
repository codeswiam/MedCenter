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
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDash extends JFrame {
    public AdminDash adminDash;
    private String adminEmail;

    private JPanel centerPanel;
    private JPanel doctorPanel;

    private JPanel topPanel;

    public final Connection connection = new DBManager().getConnection();

    public static void main(String[] args) {
        new AdminDash("admin@gmail.com");
    }

    public AdminDash(String adminEmail) {
        adminDash = this;

        this.adminEmail = adminEmail;

        setTitle("Admin Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Colors.CORNFLOWER);

        panel.add(new JLabel(), BorderLayout.WEST);
        panel.add(new JLabel(), BorderLayout.EAST);
        panel.add(new JLabel(), BorderLayout.NORTH);

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 8));
        headerPanel.setBorder(new EmptyBorder(10, 0, 10, 30));
        headerPanel.setBackground(Color.white);

        // Logo
        try {
            // load the login image
            Image image = ImageIO.read(new File("/Users/wiam/uni/java/MedCenter/resources/images/logo.png"));
            // scaling the image
            Image scaledImage = image.getScaledInstance(50, 38, Image.SCALE_SMOOTH);

            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            headerPanel.add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // empty columns
        for (int i = 0; i < 7; i++) {
            headerPanel.add(new JLabel());
        }

        // Logout Button
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 20));
        logoutButton.setBackground(Colors.BOSTON_BLUE);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setForeground(Color.white);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new WelcomePage();
            }
        });
        headerPanel.add(logoutButton);

        panel.add(headerPanel, BorderLayout.NORTH);

        // center panel

        JPanel centerPanelWrapper = new JPanel(new BorderLayout());
        centerPanelWrapper.setBackground(Colors.CORNFLOWER);
        centerPanelWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.add(centerPanelWrapper, BorderLayout.CENTER);

        centerPanel = new JPanel();
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(Color.white);
        centerPanelWrapper.add(centerPanel, BorderLayout.CENTER);

        topPanel = createTopPanel();
        centerPanel.add(topPanel, BorderLayout.NORTH);

        // doctors list
        doctorPanel = createDoctorPanel();
        doctorPanel.setBackground(Color.white);
        centerPanel.add(doctorPanel, BorderLayout.CENTER);

        add(panel);
    }

    private JPanel createTopPanel() {
        // add doctor button
        JPanel panel = new JPanel(new GridLayout(2, 5, 0, 10));
        panel.setBorder(new EmptyBorder(0, 0, 10, 10));
        panel.setBackground(Color.white);

        JPanel randPanel = new JPanel();
        randPanel.setBackground(Color.white);
        panel.add(randPanel);

        JButton addDoctor = getAddDoctorButton();
        randPanel.add(addDoctor);

        // empty columns
        for (int i = 0; i < 3; i++) {
            panel.add(new JLabel());
        }

        String query = "SELECT COUNT(*) AS doctorCount FROM doctor";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int doctorCount = resultSet.getInt("doctorCount");
                JLabel doctorCountLabel = new headerLabel("Doctor Count: " + doctorCount);
                doctorCountLabel.setForeground(Colors.BOSTON_BLUE);
                panel.add(doctorCountLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Labels
        panel.add(new headerLabel("Doctor Name"));
        panel.add(new headerLabel("Department"));
        panel.add(new headerLabel("Email"));
        panel.add(new headerLabel("Telephone Number"));
        panel.add(new JLabel()); // Empty label

        return panel;
    }

    private JPanel createDoctorPanel() {
        JPanel contentPanel = new JPanel(new GridLayout(0, 5, 0, 10)); // 5 columns, variable rows

        String query = "SELECT id, full_name, department, email, telephone FROM doctor ORDER BY full_name";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fullName = resultSet.getString("full_name");
                String department = resultSet.getString("department");
                String email = resultSet.getString("email");
                String telephone = resultSet.getString("telephone");

                JLabel fullNameLabel = new infoLabel(fullName);
                JLabel departmentLabel = new infoLabel(department);
                JLabel emailLabel = new infoLabel(email);
                JLabel telephoneLabel = new infoLabel(telephone);

                contentPanel.add(fullNameLabel);
                contentPanel.add(departmentLabel);
                contentPanel.add(emailLabel);
                contentPanel.add(telephoneLabel);

                JPanel randPanel = new JPanel();
                randPanel.setBackground(Color.white);
                contentPanel.add(randPanel);

                JButton deleteButton = getDeleteButton(id);
                randPanel.add(deleteButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.setBackground(Color.white);
        return panel;
    }

    // after deleting a doctor, we need to refresh the list of doctors
    public void refreshDoctorPanel() {
        centerPanel.removeAll();
        doctorPanel = createDoctorPanel();
        centerPanel.add(doctorPanel, BorderLayout.CENTER);
        topPanel = createTopPanel();
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.revalidate(); // Revalidate the center panel to reflect the changes
        centerPanel.repaint();
    }


    private JButton getDeleteButton(int id) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 20));
        deleteButton.setBackground(Colors.BOSTON_BLUE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setForeground(Color.white);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this doctor?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteDoctor(id);
                }
            }
        });
        return deleteButton;
    }

    private JButton getAddDoctorButton() {
        JButton addDoctor = new JButton("Add Doctor");
        addDoctor.setFont(new Font("Arial", Font.BOLD, 20));
        addDoctor.setBackground(Colors.CORNFLOWER);
        addDoctor.setOpaque(true);
        addDoctor.setBorderPainted(false);
        addDoctor.setFocusPainted(false);
        addDoctor.setForeground(Color.white);
        addDoctor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewDoctor(adminDash);
            }
        });
        return addDoctor;
    }

    public void deleteDoctor(int id) {
        try {
            String deleteQuery = "DELETE FROM doctor WHERE id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, id);
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Doctor deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the panel after deletion
                refreshDoctorPanel();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete doctor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting doctor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

class headerLabel extends JLabel {
    public headerLabel(String text) {
        super(text);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setHorizontalAlignment(CENTER);
        setForeground(Colors.BOSTON_BLUE);
        setFont(new Font("Arial", Font.BOLD, 20));
    }
}

class infoLabel extends JLabel {
    public infoLabel(String text) {
        super(text);
        setForeground(Colors.BOSTON_BLUE);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Arial", Font.PLAIN, 15));
    }
}
