package com.connextion.helpdesk.repositories;

import com.connextion.helpdesk.models.SupportUser;
import com.connextion.helpdesk.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SupportUserRepository {

    public boolean register(SupportUser user) throws SQLException {
        String insertUserSql = "INSERT INTO Support_Users (name, first_surname, second_surname, email, password, is_supervisor) VALUES (?, ?, ?, ?, ?, ?)";
        String insertServiceSql = "INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtService = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            stmtUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, user.getName());
            stmtUser.setString(2, user.getFirstSurname());
            stmtUser.setString(3, user.getSecondSurname());
            stmtUser.setString(4, user.getEmail());
            stmtUser.setString(5, user.getPassword());
            stmtUser.setBoolean(6, user.getIsSupervisor());

            int rowsAffected = stmtUser.executeUpdate();
            if (rowsAffected == 0) {
                conn.rollback();
                return false;
            }

            rs = stmtUser.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // Insert many-to-many services
            if (user.getServices() != null && !user.getServices().isEmpty()) {
                stmtService = conn.prepareStatement(insertServiceSql);
                for (Integer serviceId : user.getServices()) {
                    stmtService.setInt(1, userId);
                    stmtService.setInt(2, serviceId);
                    stmtService.addBatch();
                }
                stmtService.executeBatch();
            }

            conn.commit(); // Commit transaction
            user.setId(userId);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmtUser != null) stmtUser.close();
            if (stmtService != null) stmtService.close();
            if (conn != null) conn.close();
        }
    }

    public SupportUser login(String email, String password) throws SQLException {
        // Enforce the business rule: support user must have at least one service assigned
        String query = "SELECT DISTINCT su.id, su.name, su.first_surname, su.second_surname, su.email, su.is_supervisor " +
                       "FROM Support_Users su " +
                       "INNER JOIN Support_User_Services sus ON su.id = sus.support_user_id " +
                       "WHERE su.email = ? AND su.password = ? " +
                       "LIMIT 1";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SupportUser user = new SupportUser();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setFirstSurname(rs.getString("first_surname"));
                    user.setSecondSurname(rs.getString("second_surname"));
                    user.setEmail(rs.getString("email"));
                    user.setIsSupervisor(rs.getBoolean("is_supervisor"));
                    return user;
                }
            }
        }
        return null;
    }
}
