package client.patient;

import dao.PatientDAO;
import models.Patient;

import javax.swing.*;
import java.awt.*;

public class PatientLoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final PatientDAO patientDAO = new PatientDAO();

    public PatientLoginFrame() {
        setTitle("Patient Portal - Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> attemptLogin());

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> openRegistrationDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient patient = patientDAO.authenticate(username, password);

        if (patient != null) {
            SwingUtilities.invokeLater(() -> {
                PatientDashboardFrame dashboardFrame = new PatientDashboardFrame(patient);
                dashboardFrame.setVisible(true);
            });
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistrationDialog() {
        PatientRegistrationDialog dialog = new PatientRegistrationDialog(this);
        dialog.setVisible(true);
        Patient createdPatient = dialog.getCreatedPatient();
        if (createdPatient != null) {
            boolean saved = patientDAO.addPatient(createdPatient);
            if (saved) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please log in.", "Registration", JOptionPane.INFORMATION_MESSAGE);
                usernameField.setText(createdPatient.getUsername());
                passwordField.setText(createdPatient.getPassword());
            } else {
                JOptionPane.showMessageDialog(this, "Unable to register patient. Please check the database connection and try again.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
