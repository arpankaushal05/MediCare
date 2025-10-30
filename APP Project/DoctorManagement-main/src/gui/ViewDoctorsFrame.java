package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.DoctorDAO;
import models.Doctor;

import java.util.List;

public class ViewDoctorsFrame extends JFrame {
    private JTable doctorsTable;
    private JTable usernamesTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel usernamesTableModel;
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public ViewDoctorsFrame() {
        setTitle("View Doctors");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadDoctorsData();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new Object[]{"Doctor ID", "Name", "Specialization", "Email", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorsTable = new JTable(tableModel);
        doctorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        usernamesTableModel = new DefaultTableModel(new Object[]{"Doctor ID", "Username"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usernamesTable = new JTable(usernamesTableModel);
        usernamesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Doctor Details", new JScrollPane(doctorsTable));
        tabbedPane.addTab("Usernames", new JScrollPane(usernamesTable));

        add(tabbedPane);
    }

    private void loadDoctorsData() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();

        // Clear the table model
        tableModel.setRowCount(0);
        usernamesTableModel.setRowCount(0);

        // Populate the table with doctors data
        for (Doctor doctor : doctors) {
            tableModel.addRow(new Object[]{
                    doctor.getDoctorId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    doctor.getEmail(),
                    doctor.getPhone()
            });

            usernamesTableModel.addRow(new Object[]{
                    doctor.getDoctorId(),
                    doctor.getUsername()
            });
        }
    }
}
