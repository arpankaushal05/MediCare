package dao;

import models.AdminUser;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class AdminDAO {

    private static final List<AdminUser> FALLBACK_ADMINS = Collections.singletonList(
            new AdminUser(1, "admin", "admin@123", "System Administrator")
    );

    private AdminUser getFallbackMatch(String username, String password) {
        return FALLBACK_ADMINS.stream()
                .filter(admin -> admin.getUsername().equalsIgnoreCase(username) && admin.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public AdminUser authenticate(String username, String password) {
        String sql = "SELECT admin_id, username, password, full_name FROM admin_users WHERE username = ? AND password = ?";

        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            return getFallbackMatch(username, password);
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAdmin(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public AdminUser getByUsername(String username) {
        String sql = "SELECT admin_id, username, password, full_name FROM admin_users WHERE username = ?";

        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            return FALLBACK_ADMINS.stream()
                    .filter(admin -> admin.getUsername().equalsIgnoreCase(username))
                    .findFirst()
                    .orElse(null);
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAdmin(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private AdminUser mapAdmin(ResultSet resultSet) throws SQLException {
        return new AdminUser(
                resultSet.getInt("admin_id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("full_name")
        );
    }
}
