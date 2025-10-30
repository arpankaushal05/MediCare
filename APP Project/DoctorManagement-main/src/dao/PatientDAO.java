package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Patient;
import utils.DBConnection;

public class PatientDAO {
    
    public boolean addPatient(Patient patient) {
        String query = "INSERT INTO patients (username, password, full_name, email, phone, gender, blood_group, height_meters, allergies, disease) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // patient_id is AUTO_INCREMENT
        
        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            System.err.println("[DoctorManagement] addPatient failed: database connection unavailable");
            return false;
        }

        try (connection; PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             
            // Set parameters without patientId since it is AUTO_INCREMENT
            preparedStatement.setString(1, patient.getUsername());
            preparedStatement.setString(2, patient.getPassword());
            preparedStatement.setString(3, patient.getFullName());
            preparedStatement.setString(4, patient.getEmail());
            preparedStatement.setString(5, patient.getPhone());
            preparedStatement.setString(6, patient.getGender());
            preparedStatement.setString(7, patient.getBloodGroup());
            if (patient.getHeightMeters() != null) {
                preparedStatement.setDouble(8, patient.getHeightMeters());
            } else {
                preparedStatement.setNull(8, java.sql.Types.DOUBLE);
            }
            preparedStatement.setString(9, patient.getAllergies());
            preparedStatement.setString(10, patient.getDisease());
            
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT patient_id, username, password, full_name, email, phone, gender, blood_group, height_meters, allergies, disease " +
                "FROM patients ORDER BY full_name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                patients.add(mapPatient(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }

    public Patient getPatientById(int patientId) {
        String query = "SELECT patient_id, username, password, full_name, email, phone, gender, blood_group, height_meters, allergies, disease " +
                "FROM patients WHERE patient_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, patientId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPatient(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Patient authenticate(String username, String password) {
        String query = "SELECT patient_id, username, password, full_name, email, phone, gender, blood_group, height_meters, allergies, disease " +
                "FROM patients WHERE username = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPatient(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Patient mapPatient(ResultSet resultSet) throws SQLException {
        int patientId = resultSet.getInt("patient_id");
        Double heightMeters = resultSet.getObject("height_meters") != null ? resultSet.getDouble("height_meters") : null;

        Patient patient = new Patient(
                patientId,
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("gender"),
                resultSet.getString("blood_group"),
                heightMeters,
                resultSet.getString("allergies"),
                resultSet.getString("disease")
        );

        return patient;
    }
}
