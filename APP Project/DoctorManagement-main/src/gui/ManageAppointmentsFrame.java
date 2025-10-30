package gui;

import dao.AppointmentDAO;
import models.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ManageAppointmentsFrame extends JFrame {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DefaultTableModel tableModel;

    public ManageAppointmentsFrame() {
        setTitle("Manage Appointments");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblDoctorId = new JLabel("Doctor ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblDoctorId, gbc);

        JTextField txtDoctorId = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(txtDoctorId, gbc);

        JLabel lblPatientId = new JLabel("Patient ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblPatientId, gbc);

        JTextField txtPatientId = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtPatientId, gbc);

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblDate, gbc);

        JTextField txtDate = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(txtDate, gbc);

        JLabel lblTime = new JLabel("Time (HH:MM):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblTime, gbc);

        JTextField txtTime = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(txtTime, gbc);

        JLabel lblNotes = new JLabel("Notes:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(lblNotes, gbc);

        JTextField txtNotes = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(txtNotes, gbc);

        JButton btnBookAppointment = new JButton("Book Appointment");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnBookAppointment, gbc);

        btnBookAppointment.addActionListener(e -> {
            try {
                int doctorId = Integer.parseInt(txtDoctorId.getText().trim());
                int patientId = Integer.parseInt(txtPatientId.getText().trim());
                LocalDate appointmentDate = LocalDate.parse(txtDate.getText().trim());
                LocalTime appointmentTime = LocalTime.parse(txtTime.getText().trim());
                String notes = txtNotes.getText().trim();

                Appointment appointment = new Appointment(doctorId, patientId, appointmentDate, appointmentTime, notes);
                appointmentDAO.addAppointment(appointment);

                JOptionPane.showMessageDialog(this, "Appointment booked successfully.");

                txtDoctorId.setText("");
                txtPatientId.setText("");
                txtDate.setText("");
                txtTime.setText("");
                txtNotes.setText("");

                loadAppointments();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Doctor ID and Patient ID must be numbers.", "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter date/time in the correct format.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Doctor", "Patient", "Date", "Time", "Notes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable appointmentsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
        add(tableScrollPane, BorderLayout.CENTER);

        loadAppointments();

        setVisible(true);
    }

    private void loadAppointments() {
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        tableModel.setRowCount(0);
        for (Appointment appointment : appointments) {
            tableModel.addRow(new Object[]{
                    appointment.getAppointmentId(),
                    appointment.getDoctorId(),
                    appointment.getPatientId(),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    appointment.getNotes()
            });
        }
    }
}

