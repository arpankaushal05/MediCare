package gui;

import dao.PatientDAO;
import models.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ViewPatientsFrame extends JFrame {
    private final DefaultTableModel patientsTableModel;
    private final PatientDAO patientDAO = new PatientDAO();

    public ViewPatientsFrame() {
        setTitle("View Patients");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        patientsTableModel = new DefaultTableModel(new Object[]{
                "Patient ID",
                "Username",
                "Full Name",
                "Email",
                "Phone",
                "Gender",
                "Blood Group",
                "Height (m)",
                "Allergies",
                "Disease"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable patientsTable = new JTable(patientsTableModel);
        patientsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        patientsTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        patientsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        patientsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        patientsTable.getColumnModel().getColumn(3).setPreferredWidth(190);
        patientsTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        patientsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        patientsTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        patientsTable.getColumnModel().getColumn(7).setPreferredWidth(90);
        patientsTable.getColumnModel().getColumn(8).setPreferredWidth(160);
        patientsTable.getColumnModel().getColumn(9).setPreferredWidth(160);
        patientsTable.setFillsViewportHeight(true);

        add(new JScrollPane(patientsTable));

        loadPatients();
    }

    private void loadPatients() {
        patientsTableModel.setRowCount(0);
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientsTableModel.addRow(new Object[]{
                    patient.getPatientId(),
                    patient.getUsername(),
                    patient.getFullName(),
                    patient.getEmail(),
                    patient.getPhone(),
                    defaultValue(patient.getGender()),
                    defaultValue(patient.getBloodGroup()),
                    patient.getHeightMeters() != null ? String.format("%.2f", patient.getHeightMeters()) : "-",
                    defaultValue(patient.getAllergies()),
                    defaultValue(patient.getDisease())
            });
        }
    }

    private String defaultValue(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}
