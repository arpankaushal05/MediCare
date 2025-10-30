package dao;

import models.Doctor;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DoctorDAO {
    private static final List<Doctor> FALLBACK_DOCTORS;
    private static final AtomicBoolean FALLBACK_WARNING_PRINTED = new AtomicBoolean(false);

    static {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor(1, "fallback_doc1", "password", "Dr. Aisha Kapoor", "Cardiologist", "aisha.kapoor@example.com", "+91-9876543210"));
        doctors.add(new Doctor(2, "fallback_doc2", "password", "Dr. Rohan Mehta", "Dermatologist", "rohan.mehta@example.com", "+91-9123456780"));
        doctors.add(new Doctor(3, "fallback_doc3", "password", "Dr. Neha Singh", "Pediatrician", "neha.singh@example.com", "+91-9988776655"));
        doctors.add(new Doctor(4, "fallback_doc4", "password", "Dr. Sameer Joshi", "Neurologist", "sameer.joshi@example.com", "+91-9011223344"));
        doctors.add(new Doctor(5, "fallback_doc5", "password", "Dr. Kavita Rao", "Orthopedic Surgeon", "kavita.rao@example.com", "+91-9345612780"));
        doctors.add(new Doctor(6, "fallback_doc6", "password", "Dr. Farhan Ali", "Psychiatrist", "farhan.ali@example.com", "+91-9487654321"));
        FALLBACK_DOCTORS = Collections.unmodifiableList(doctors);
    }

    private void logFallback(SQLException e) {
        if (FALLBACK_WARNING_PRINTED.compareAndSet(false, true)) {
            System.err.println("[DoctorManagement] Database unavailable; using in-memory doctor list. Details: " + e.getMessage());
        }
    }

    private Doctor getFallbackByCredentials(String username, String password) {
        return FALLBACK_DOCTORS.stream()
                .filter(doctor -> doctor.getUsername().equalsIgnoreCase(username) && doctor.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    private Doctor getFallbackByUsername(String username) {
        return FALLBACK_DOCTORS.stream()
                .filter(doctor -> doctor.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();

        String sql = "SELECT doctor_id, username, password, name, specialization, email, phone FROM doctors ORDER BY name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                doctors.add(mapDoctor(resultSet));
            }
        } catch (SQLException e) {
            logFallback(e);
            return new ArrayList<>(FALLBACK_DOCTORS);
        }

        return doctors;
    }

    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT doctor_id, username, password, name, specialization, email, phone FROM doctors WHERE doctor_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, doctorId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDoctor(resultSet);
                }
            }
        } catch (SQLException e) {
            logFallback(e);
            return FALLBACK_DOCTORS.stream()
                    .filter(d -> d.getDoctorId() == doctorId)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public Doctor getDoctorByUsername(String username) {
        String sql = "SELECT doctor_id, username, password, name, specialization, email, phone FROM doctors WHERE username = ?";

        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            return getFallbackByUsername(username);
        }

        try (connection; PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDoctor(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Doctor authenticate(String username, String password) {
        String sql = "SELECT doctor_id, username, password, name, specialization, email, phone FROM doctors WHERE username = ? AND password = ?";

        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            return getFallbackByCredentials(username, password);
        }

        try (connection; PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDoctor(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addDoctor(Doctor newDoctor) {
        String sql = "INSERT INTO doctors (username, password, name, specialization, email, phone) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newDoctor.getUsername());
            statement.setString(2, newDoctor.getPassword());
            statement.setString(3, newDoctor.getName());
            statement.setString(4, newDoctor.getSpecialization());
            statement.setString(5, newDoctor.getEmail());
            statement.setString(6, newDoctor.getPhone());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add other CRUD methods for Doctor as needed

    private Doctor mapDoctor(ResultSet resultSet) throws SQLException {
        return new Doctor(
                resultSet.getInt("doctor_id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("name"),
                resultSet.getString("specialization"),
                resultSet.getString("email"),
                resultSet.getString("phone")
        );
    }
}