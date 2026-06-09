package com.connextion.helpdesk.repositories;

import com.connextion.helpdesk.models.Comment;
import com.connextion.helpdesk.models.Issue;
import com.connextion.helpdesk.models.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IssueRepository {

    @Autowired
    private DataSource dataSource;

    // Check if client has the service registered
    public boolean hasService(int clientId, int serviceId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Client_Services WHERE client_id = ? AND service_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // CU4: Create Ticket
    public boolean create(Issue issue) throws SQLException {
        String query = "INSERT INTO Issues (description, contact_phone, contact_email, address, status, classification, client_id, service_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, issue.getDescription());
            stmt.setString(2, issue.getContactPhone());
            stmt.setString(3, issue.getContactEmail());
            stmt.setString(4, issue.getAddress());
            stmt.setString(5, issue.getStatus() != null ? issue.getStatus() : "Ingresado");
            stmt.setString(6, issue.getClassification() != null ? issue.getClassification() : "Media");
            stmt.setInt(7, issue.getClientId());
            stmt.setInt(8, issue.getServiceId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        issue.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // CU5: Get tickets list for client
    public List<Issue> getByClientId(int clientId) throws SQLException {
        List<Issue> list = new ArrayList<>();
        String query = "SELECT i.id, i.description, i.contact_phone, i.contact_email, i.address, i.status, i.classification, i.client_id, i.service_id, i.support_user_assigned_id, i.resolution_comment, i.register_timestamp, s.name as service_name " +
                       "FROM Issues i " +
                       "INNER JOIN Services s ON i.service_id = s.id " +
                       "WHERE i.client_id = ? " +
                       "ORDER BY i.register_timestamp DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Issue i = new Issue();
                    i.setId(rs.getInt("id"));
                    i.setDescription(rs.getString("description"));
                    i.setContactPhone(rs.getString("contact_phone"));
                    i.setContactEmail(rs.getString("contact_email"));
                    i.setAddress(rs.getString("address"));
                    i.setStatus(rs.getString("status"));
                    i.setClassification(rs.getString("classification"));
                    i.setClientId(rs.getInt("client_id"));
                    i.setServiceId(rs.getInt("service_id"));
                    i.setSupportUserAssignedId(rs.getObject("support_user_assigned_id") != null ? rs.getInt("support_user_assigned_id") : null);
                    i.setResolutionComment(rs.getString("resolution_comment"));
                    i.setRegisterTimestamp(rs.getTimestamp("register_timestamp"));
                    i.setServiceName(rs.getString("service_name"));
                    list.add(i);
                }
            }
        }
        return list;
    }

    // CU6 and CU10: Get issue by ID (with details)
    public Issue getById(int id) throws SQLException {
        String query = "SELECT i.id, i.description, i.contact_phone, i.contact_email, i.address, i.status, i.classification, i.client_id, i.service_id, i.support_user_assigned_id, i.resolution_comment, i.register_timestamp, " +
                       "c.name + ' ' + c.first_surname + ' ' + c.second_surname as client_name, " +
                       "s.name as service_name, " +
                       "su.name + ' ' + su.first_surname as supporter_name " +
                       "FROM Issues i " +
                       "INNER JOIN Clients c ON i.client_id = c.id " +
                       "INNER JOIN Services s ON i.service_id = s.id " +
                       "LEFT JOIN Support_Users su ON i.support_user_assigned_id = su.id " +
                       "WHERE i.id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Issue i = new Issue();
                    i.setId(rs.getInt("id"));
                    i.setDescription(rs.getString("description"));
                    i.setContactPhone(rs.getString("contact_phone"));
                    i.setContactEmail(rs.getString("contact_email"));
                    i.setAddress(rs.getString("address"));
                    i.setStatus(rs.getString("status"));
                    i.setClassification(rs.getString("classification"));
                    i.setClientId(rs.getInt("client_id"));
                    i.setServiceId(rs.getInt("service_id"));
                    i.setSupportUserAssignedId(rs.getObject("support_user_assigned_id") != null ? rs.getInt("support_user_assigned_id") : null);
                    i.setResolutionComment(rs.getString("resolution_comment"));
                    i.setRegisterTimestamp(rs.getTimestamp("register_timestamp"));
                    i.setClientName(rs.getString("client_name"));
                    i.setServiceName(rs.getString("service_name"));
                    i.setSupportUserAssignedName(rs.getString("supporter_name") != null ? rs.getString("supporter_name") : "Sin asignar");
                    return i;
                }
            }
        }
        return null;
    }

    // CU10: List all issues for support (sorted ascending by default)
    public List<Issue> getAll() throws SQLException {
        List<Issue> list = new ArrayList<>();
        String query = "SELECT i.id, i.description, i.contact_phone, i.contact_email, i.address, i.status, i.classification, i.client_id, i.service_id, i.support_user_assigned_id, i.resolution_comment, i.register_timestamp, " +
                       "s.name as service_name, " +
                       "su.name + ' ' + su.first_surname as supporter_name " +
                       "FROM Issues i " +
                       "INNER JOIN Services s ON i.service_id = s.id " +
                       "LEFT JOIN Support_Users su ON i.support_user_assigned_id = su.id " +
                       "ORDER BY i.id ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Issue i = new Issue();
                i.setId(rs.getInt("id"));
                i.setDescription(rs.getString("description"));
                i.setContactPhone(rs.getString("contact_phone"));
                i.setContactEmail(rs.getString("contact_email"));
                i.setAddress(rs.getString("address"));
                i.setStatus(rs.getString("status"));
                i.setClassification(rs.getString("classification"));
                i.setClientId(rs.getInt("client_id"));
                i.setServiceId(rs.getInt("service_id"));
                i.setSupportUserAssignedId(rs.getObject("support_user_assigned_id") != null ? rs.getInt("support_user_assigned_id") : null);
                i.setResolutionComment(rs.getString("resolution_comment"));
                i.setRegisterTimestamp(rs.getTimestamp("register_timestamp"));
                i.setServiceName(rs.getString("service_name"));
                i.setSupportUserAssignedName(rs.getString("supporter_name") != null ? rs.getString("supporter_name") : "Sin asignar");
                list.add(i);
            }
        }
        return list;
    }

    // CU6: Add Comment
    public boolean addComment(Comment comment) throws SQLException {
        String query = "INSERT INTO Comments (description, issue_id, user_type, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, comment.getDescription());
            stmt.setInt(2, comment.getIssueId());
            stmt.setString(3, comment.getUserType());
            stmt.setInt(4, comment.getUserId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        comment.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // CU6 details: Get Comments for an issue sorted ascending by timestamp
    public List<Comment> getCommentsByIssueId(int issueId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        // Query getting client or supporter names depending on user_type
        String query = "SELECT c.id, c.description, c.comment_timestamp, c.issue_id, c.user_type, c.user_id, " +
                       "CASE " +
                       "  WHEN c.user_type = 'CLIENT' THEN cl.name + ' ' + cl.first_surname " +
                       "  ELSE su.name + ' ' + su.first_surname " +
                       "END as user_name " +
                       "FROM Comments c " +
                       "LEFT JOIN Clients cl ON c.user_type = 'CLIENT' AND c.user_id = cl.id " +
                       "LEFT JOIN Support_Users su ON c.user_type = 'SUPPORT' AND c.user_id = su.id " +
                       "WHERE c.issue_id = ? " +
                       "ORDER BY c.comment_timestamp ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setId(rs.getInt("id"));
                    c.setDescription(rs.getString("description"));
                    c.setCommentTimestamp(rs.getTimestamp("comment_timestamp"));
                    c.setIssueId(rs.getInt("issue_id"));
                    c.setUserType(rs.getString("user_type"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setUserName(rs.getString("user_name"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    // Notes (CU13)
    public boolean addNote(Note note) throws SQLException {
        String query = "INSERT INTO Notes (description, issue_id, support_user_id) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, note.getDescription());
            stmt.setInt(2, note.getIssueId());
            stmt.setInt(3, note.getSupportUserId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        note.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Get Notes for an issue
    public List<Note> getNotesByIssueId(int issueId) throws SQLException {
        List<Note> list = new ArrayList<>();
        String query = "SELECT n.id, n.description, n.note_timestamp, n.issue_id, n.support_user_id, su.name + ' ' + su.first_surname as supporter_name " +
                       "FROM Notes n " +
                       "INNER JOIN Support_Users su ON n.support_user_id = su.id " +
                       "WHERE n.issue_id = ? " +
                       "ORDER BY n.note_timestamp ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Note n = new Note();
                    n.setId(rs.getInt("id"));
                    n.setDescription(rs.getString("description"));
                    n.setNoteTimestamp(rs.getTimestamp("note_timestamp"));
                    n.setIssueId(rs.getInt("issue_id"));
                    n.setSupportUserId(rs.getInt("support_user_id"));
                    n.setSupportUserName(rs.getString("supporter_name"));
                    list.add(n);
                }
            }
        }
        return list;
    }
}
