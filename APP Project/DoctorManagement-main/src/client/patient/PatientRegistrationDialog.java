package client.patient;

import models.Patient;

import javax.swing.*;
import java.awt.*;

public class PatientRegistrationDialog extends JDialog {
    private final JTextField fullNameField = new JTextField(20);
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField genderField = new JTextField(20);
    private final JTextField bloodGroupField = new JTextField(20);
    private final JTextField heightField = new JTextField(20);
    private final JTextField allergiesField = new JTextField(20);
    private final JTextField diseaseField = new JTextField(20);
    private Patient createdPatient;

    public PatientRegistrationDialog(Frame owner) {
        super(owner, "Patient Registration", true);
        setSize(450, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addField(form, gbc, row++, "Full Name:", fullNameField);
        addField(form, gbc, row++, "Username:", usernameField);
        addField(form, gbc, row++, "Password:", passwordField);
        addField(form, gbc, row++, "Email:", emailField);
        addField(form, gbc, row++, "Phone:", phoneField);
        addField(form, gbc, row++, "Gender:", genderField);
        addField(form, gbc, row++, "Blood Group:", bloodGroupField);
        addField(form, gbc, row++, "Height (m):", heightField);
        addField(form, gbc, row++, "Allergies:", allergiesField);
        addField(form, gbc, row++, "Disease:", diseaseField);

        add(new JScrollPane(form), BorderLayout.CENTER);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(event -> attemptRegistration());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> {
            createdPatient = null;
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);
    }

    private void attemptRegistration() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = genderField.getText().trim();
        String bloodGroup = bloodGroupField.getText().trim();
        String heightValue = heightField.getText().trim();
        String allergies = allergiesField.getText().trim();
        String disease = diseaseField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name, username, password, email, and phone are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Double heightMeters = null;
        if (!heightValue.isEmpty()) {
            try {
                heightMeters = Double.parseDouble(heightValue);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Height must be a valid number (e.g., 1.73).", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        createdPatient = new Patient(username, password, fullName, email, phone, gender, bloodGroup, heightMeters, allergies, disease);
        dispose();
    }

    public Patient getCreatedPatient() {
        return createdPatient;
    }
}
