package dao;

import models.ChatConversation;
import models.ChatMessage;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

    public ChatConversation createConversation(int patientId, int doctorId) {
        String insertSql = "INSERT INTO chat_conversations (patient_id, doctor_id, status) VALUES (?, ?, 'OPEN')";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int conversationId = generatedKeys.getInt(1);
                    return getConversationById(conversationId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ChatConversation getConversationById(int conversationId) {
        String query = "SELECT conversation_id, patient_id, doctor_id, status, created_at, updated_at FROM chat_conversations WHERE conversation_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, conversationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapConversation(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ChatConversation> getConversationsForPatient(int patientId) {
        String query = "SELECT conversation_id, patient_id, doctor_id, status, created_at, updated_at FROM chat_conversations WHERE patient_id = ? ORDER BY updated_at DESC";
        return getConversationsByQuery(query, patientId);
    }

    public List<ChatConversation> getConversationsForDoctor(int doctorId) {
        String query = "SELECT conversation_id, patient_id, doctor_id, status, created_at, updated_at FROM chat_conversations WHERE doctor_id = ? ORDER BY updated_at DESC";
        return getConversationsByQuery(query, doctorId);
    }

    public List<ChatConversation> getAllConversations() {
        String query = "SELECT conversation_id, patient_id, doctor_id, status, created_at, updated_at FROM chat_conversations ORDER BY updated_at DESC";
        return getConversationsByQuery(query, null);
    }

    private List<ChatConversation> getConversationsByQuery(String query, Integer id) {
        List<ChatConversation> conversations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (query.contains("WHERE") && id != null) {
                statement.setInt(1, id);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    conversations.add(mapConversation(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conversations;
    }

    public List<ChatMessage> getMessagesForConversation(int conversationId) {
        List<ChatMessage> messages = new ArrayList<>();
        String query = "SELECT message_id, conversation_id, sender_type, sender_id, message_text, sent_at FROM chat_messages WHERE conversation_id = ? ORDER BY sent_at";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, conversationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(mapMessage(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public ChatMessage addMessage(ChatMessage message) {
        String insertSql = "INSERT INTO chat_messages (conversation_id, sender_type, sender_id, message_text) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, message.getConversationId());
            statement.setString(2, message.getSenderType());
            statement.setInt(3, message.getSenderId());
            statement.setString(4, message.getMessageText());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int messageId = generatedKeys.getInt(1);
                    message.setMessageId(messageId);
                    message.setSentAt(LocalDateTime.now());
                    touchConversation(message.getConversationId());
                    return message;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateConversationStatus(int conversationId, String status) {
        String updateSql = "UPDATE chat_conversations SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE conversation_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSql)) {

            statement.setString(1, status);
            statement.setInt(2, conversationId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void touchConversation(int conversationId) {
        String sql = "UPDATE chat_conversations SET updated_at = CURRENT_TIMESTAMP WHERE conversation_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, conversationId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ChatConversation mapConversation(ResultSet resultSet) throws SQLException {
        int conversationId = resultSet.getInt("conversation_id");
        int patientId = resultSet.getInt("patient_id");
        int doctorId = resultSet.getInt("doctor_id");
        String status = resultSet.getString("status");
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        return new ChatConversation(
                conversationId,
                patientId,
                doctorId,
                status,
                createdAt != null ? createdAt.toLocalDateTime() : null,
                updatedAt != null ? updatedAt.toLocalDateTime() : null
        );
    }

    private ChatMessage mapMessage(ResultSet resultSet) throws SQLException {
        int messageId = resultSet.getInt("message_id");
        int conversationId = resultSet.getInt("conversation_id");
        String senderType = resultSet.getString("sender_type");
        int senderId = resultSet.getInt("sender_id");
        String messageText = resultSet.getString("message_text");
        Timestamp sentAt = resultSet.getTimestamp("sent_at");

        return new ChatMessage(
                messageId,
                conversationId,
                senderType,
                senderId,
                messageText,
                sentAt != null ? sentAt.toLocalDateTime() : null
        );
    }
}
