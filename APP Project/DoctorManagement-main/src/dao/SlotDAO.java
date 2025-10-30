package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date; // Import for java.sql.Date
import models.Slot;
import utils.DBConnection;

public class SlotDAO {
    
    public void addSlot(Slot slot) {
        String query = "INSERT INTO slots (doctor_id, slot_date, slot_time, is_booked) VALUES (?, ?, ?, ?)"; // Updated query
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             
            // Set parameters without slotId since it is AUTO_INCREMENT
            preparedStatement.setInt(1, slot.getDoctorId());
            preparedStatement.setDate(2, Date.valueOf(slot.getSlotDate())); // Convert String to java.sql.Date
            preparedStatement.setString(3, slot.getSlotTime());
            preparedStatement.setBoolean(4, slot.isBooked()); // Include isBooked
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
