package com.connextion.helpdesk.repositories;

import com.connextion.helpdesk.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class ClientRepository {

    @Autowired
    private DataSource dataSource;

    public boolean register(Client client) throws SQLException {
        String insertClientSql = "INSERT INTO Clients (name, first_surname, second_surname, email, password, address, phone, second_contact) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertServiceSql = "INSERT INTO Client_Services (client_id, service_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmtClient = null;
        PreparedStatement stmtService = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Start transaction

            stmtClient = conn.prepareStatement(insertClientSql, Statement.RETURN_GENERATED_KEYS);
            stmtClient.setString(1, client.getName());
            stmtClient.setString(2, client.getFirstSurname());
            stmtClient.setString(3, client.getSecondSurname());
            stmtClient.setString(4, client.getEmail());
            stmtClient.setString(5, client.getPassword());
            stmtClient.setString(6, client.getAddress());
            stmtClient.setString(7, client.getPhone());
            stmtClient.setString(8, client.getSecondContact());

            int rowsAffected = stmtClient.executeUpdate();
            if (rowsAffected == 0) {
                conn.rollback();
                return false;
            }

            rs = stmtClient.getGeneratedKeys();
            int clientId = 0;
            if (rs.next()) {
                clientId = rs.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // Insert services
            if (client.getServices() != null && !client.getServices().isEmpty()) {
                stmtService = conn.prepareStatement(insertServiceSql);
                for (Integer serviceId : client.getServices()) {
                    stmtService.setInt(1, clientId);
                    stmtService.setInt(2, serviceId);
                    stmtService.addBatch();
                }
                stmtService.executeBatch();
            }

            conn.commit(); // Commit transaction
            client.setId(clientId);
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
            if (stmtClient != null) stmtClient.close();
            if (stmtService != null) stmtService.close();
            if (conn != null) conn.close();
        }
    }

    public Client login(String email, String password) throws SQLException {
        String query = "SELECT id, name, first_surname, second_surname, email, address, phone, second_contact FROM Clients WHERE email = ? AND password = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setName(rs.getString("name"));
                    client.setFirstSurname(rs.getString("first_surname"));
                    client.setSecondSurname(rs.getString("second_surname"));
                    client.setEmail(rs.getString("email"));
                    client.setAddress(rs.getString("address"));
                    client.setPhone(rs.getString("phone"));
                    client.setSecondContact(rs.getString("second_contact"));
                    return client;
                }
            }
        }
        return null;
    }

    public java.util.List<java.util.Map<String, Object>> getClientServices(int clientId) throws SQLException {
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        String query = "SELECT s.id, s.name FROM Client_Services cs INNER JOIN Services s ON cs.service_id = s.id WHERE cs.client_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("name", rs.getString("name"));
                    list.add(map);
                }
            }
        }
        return list;
    }
}
