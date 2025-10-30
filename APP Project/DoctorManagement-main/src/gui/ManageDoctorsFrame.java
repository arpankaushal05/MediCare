package gui;

import dao.DoctorDAO;
import models.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Arrays;

public class ManageDoctorsFrame extends JFrame {
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private DefaultTableModel tableModel;

    public ManageDoctorsFrame() {
        setTitle("Manage Doctors");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);

        JTextField txtUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblPassword, gbc);

        JPasswordField txtPassword = new JPasswordField(20);
        txtPassword.setEchoChar('•');
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtPassword, gbc);

        JLabel lblName = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblName, gbc);

        JTextField txtName = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(txtName, gbc);

        JLabel lblSpecialization = new JLabel("Specialization:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblSpecialization, gbc);

        JTextField txtSpecialization = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(txtSpecialization, gbc);

        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(lblEmail, gbc);

        JTextField txtEmail = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(txtEmail, gbc);

        JLabel lblPhone = new JLabel("Phone:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(lblPhone, gbc);

        JTextField txtPhone = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(txtPhone, gbc);

        JButton btnAddDoctor = new JButton("Add Doctor");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnAddDoctor, gbc);

        btnAddDoctor.addActionListener(event -> {
            String formUsername = txtUsername.getText().trim();
            String formPassword = new String(txtPassword.getPassword()).trim();
            String formName = txtName.getText().trim();
            String formSpecialization = txtSpecialization.getText().trim();
            String formEmail = txtEmail.getText().trim();
            String formPhone = txtPhone.getText().trim();

            if (formUsername.isEmpty() || formPassword.isEmpty() || formName.isEmpty() || formSpecialization.isEmpty() || formEmail.isEmpty() || formPhone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Doctor createdDoctor = new Doctor(formUsername, formPassword, formName, formSpecialization, formEmail, formPhone);
            doctorDAO.addDoctor(createdDoctor);
            JOptionPane.showMessageDialog(this, "Doctor added: " + formName);

            txtUsername.setText("");
            txtPassword.setText("");
            txtName.setText("");
            txtSpecialization.setText("");
            txtEmail.setText("");
            txtPhone.setText("");

            loadDoctors();
        });

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Specialization", "Email", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable doctorTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(doctorTable);
        add(tableScrollPane, BorderLayout.CENTER);

        loadDoctors();

        setVisible(true);
    }

    private void loadDoctors() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        tableModel.setRowCount(0);
        for (Doctor doctor : doctors) {
            tableModel.addRow(new Object[]{
                    doctor.getDoctorId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    doctor.getEmail(),
                    doctor.getPhone()
            });
        }
    }
}