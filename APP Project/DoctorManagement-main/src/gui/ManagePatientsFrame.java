package gui;

import dao.PatientDAO;
import models.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManagePatientsFrame extends JFrame {
    private final PatientDAO patientDAO = new PatientDAO();
    private DefaultTableModel tableModel;

    public ManagePatientsFrame() {
        setTitle("Manage Patients");
        setSize(550, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFullName = new JLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblFullName, gbc);

        JTextField txtFullName = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(txtFullName, gbc);

        JLabel lblUsername = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblUsername, gbc);

        JTextField txtUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblPassword, gbc);

        JPasswordField txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(txtPassword, gbc);

        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblEmail, gbc);

        JTextField txtEmail = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(txtEmail, gbc);

        JLabel lblPhone = new JLabel("Phone:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(lblPhone, gbc);

        JTextField txtPhone = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(txtPhone, gbc);

        JLabel lblGender = new JLabel("Gender:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(lblGender, gbc);

        JTextField txtGender = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(txtGender, gbc);

        JLabel lblBloodGroup = new JLabel("Blood Group:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(lblBloodGroup, gbc);

        JTextField txtBloodGroup = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 6;
        formPanel.add(txtBloodGroup, gbc);

        JLabel lblHeight = new JLabel("Height (m):");
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(lblHeight, gbc);

        JTextField txtHeight = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 7;
        formPanel.add(txtHeight, gbc);

        JLabel lblAllergies = new JLabel("Allergies:");
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(lblAllergies, gbc);

        JTextField txtAllergies = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 8;
        formPanel.add(txtAllergies, gbc);

        JLabel lblDisease = new JLabel("Disease:");
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(lblDisease, gbc);

        JTextField txtDisease = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 9;
        formPanel.add(txtDisease, gbc);

        JButton btnAddPatient = new JButton("Add Patient");
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnAddPatient, gbc);

        btnAddPatient.addActionListener(e -> {
            String fullName = txtFullName.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String gender = txtGender.getText().trim();
            String bloodGroup = txtBloodGroup.getText().trim();
            String heightValue = txtHeight.getText().trim();
            String allergies = txtAllergies.getText().trim();
            String disease = txtDisease.getText().trim();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
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

            Patient newPatient = new Patient(username, password, fullName, email, phone, gender, bloodGroup, heightMeters, allergies, disease);
            boolean added = patientDAO.addPatient(newPatient);
            if (!added) {
                JOptionPane.showMessageDialog(this, "Unable to add patient. Please verify the database connection/schema.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Patient added: " + fullName);

            txtFullName.setText("");
            txtUsername.setText("");
            txtPassword.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            txtGender.setText("");
            txtBloodGroup.setText("");
            txtHeight.setText("");
            txtAllergies.setText("");
            txtDisease.setText("");

            loadPatients();
        });

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Full Name", "Username", "Email", "Phone", "Gender", "Blood Group", "Height (m)", "Allergies", "Disease"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable patientsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(patientsTable);
        add(tableScrollPane, BorderLayout.CENTER);

        loadPatients();

        setVisible(true);
    }

    private void loadPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        tableModel.setRowCount(0);
        for (Patient patient : patients) {
            tableModel.addRow(new Object[]{
                    patient.getPatientId(),
                    patient.getFullName(),
                    patient.getUsername(),
                    patient.getEmail(),
                    patient.getPhone(),
                    patient.getGender(),
                    patient.getBloodGroup(),
                    patient.getHeightMeters(),
                    patient.getAllergies(),
                    patient.getDisease()
            });
        }
    }
}

