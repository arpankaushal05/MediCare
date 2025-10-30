package dao;

import models.PatientHistory;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PatientHistoryDAO {

    public int addHistory(PatientHistory history) {
        String query = "INSERT INTO patient_history (patient_id, doctor_id, visit_date, diagnosis, treatment, notes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, history.getPatientId());
            statement.setInt(2, history.getDoctorId());
            statement.setDate(3, Date.valueOf(history.getVisitDate()));
            statement.setString(4, history.getDiagnosis());
            statement.setString(5, history.getTreatment());
            statement.setString(6, history.getNotes());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    history.setHistoryId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<PatientHistory> getAllHistory() {
        List<PatientHistory> historyEntries = new ArrayList<>();
        String query = "SELECT history_id, patient_id, doctor_id, visit_date, diagnosis, treatment, notes FROM patient_history ORDER BY visit_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                PatientHistory history = new PatientHistory(
                        resultSet.getInt("patient_id"),
                        resultSet.getInt("doctor_id"),
                        resultSet.getDate("visit_date").toLocalDate(),
                        resultSet.getString("diagnosis"),
                        resultSet.getString("treatment"),
                        resultSet.getString("notes")
                );
                history.setHistoryId(resultSet.getInt("history_id"));
                historyEntries.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyEntries;
    }

    public List<PatientHistory> getHistoryForPatient(int patientId) {
        List<PatientHistory> historyEntries = new ArrayList<>();
        String query = "SELECT history_id, doctor_id, visit_date, diagnosis, treatment, notes FROM patient_history WHERE patient_id = ? ORDER BY visit_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PatientHistory history = new PatientHistory(
                            patientId,
                            resultSet.getInt("doctor_id"),
                            resultSet.getDate("visit_date").toLocalDate(),
                            resultSet.getString("diagnosis"),
                            resultSet.getString("treatment"),
                            resultSet.getString("notes")
                    );
                    history.setHistoryId(resultSet.getInt("history_id"));
                    historyEntries.add(history);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyEntries;
    }
}
