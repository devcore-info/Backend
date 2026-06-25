package com.connextion.helpdesk.repositories;

import com.connextion.helpdesk.models.Comment;
import com.connextion.helpdesk.models.Issue;
import com.connextion.helpdesk.models.Note;
import com.connextion.helpdesk.models.Bitacora;
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

    public boolean assignSupportUser(int id, int supportUserId) throws SQLException {
        String query = "UPDATE Issues SET support_user_assigned_id = ?, status = CASE WHEN status = 'Ingresado' THEN 'Asignado' ELSE status END WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, supportUserId);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int id, String status, String resolutionComment) throws SQLException {
        String query = "UPDATE Issues SET status = ?, resolution_comment = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, resolutionComment);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Bitacora methods for logging transitions
    public boolean addBitacora(Bitacora entry) throws SQLException {
        String query = "INSERT INTO Bitacora (issue_id, changed_by, action_type, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entry.getIssueId());
            stmt.setString(2, entry.getChangedBy());
            stmt.setString(3, entry.getActionType());
            stmt.setString(4, entry.getDescription());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        entry.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Bitacora> getBitacoraByIssueId(int issueId) throws SQLException {
        List<Bitacora> list = new ArrayList<>();
        String query = "SELECT id, issue_id, changed_by, action_type, description, change_timestamp " +
                       "FROM Bitacora WHERE issue_id = ? " +
                       "ORDER BY change_timestamp ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bitacora b = new Bitacora();
                    b.setIssueId(rs.getInt("issue_id"));
                    b.setChangedBy(rs.getString("changed_by"));
                    b.setActionType(rs.getString("action_type"));
                    b.setDescription(rs.getString("description"));
                    b.setChangeTimestamp(rs.getTimestamp("change_timestamp"));
                    list.add(b);
                }
            }
        }
        return list;
    }

    public boolean updateClassificationAndAssignee(int id, String classification, Integer supportUserId) throws SQLException {
        String query = "UPDATE Issues SET classification = ?, support_user_assigned_id = ?, status = 'Asignado' WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, classification);
            if (supportUserId != null) {
                stmt.setInt(2, supportUserId);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Integer findBestSupporterForService(int serviceId) throws SQLException {
        String query = "SELECT TOP 1 su.id " +
                       "FROM Support_Users su " +
                       "INNER JOIN Support_User_Services sus ON su.id = sus.support_user_id " +
                       "LEFT JOIN Issues i ON su.id = i.support_user_assigned_id AND i.status != 'Resuelto' " +
                       "WHERE sus.service_id = ? AND su.is_supervisor = 0 " +
                       "GROUP BY su.id " +
                       "ORDER BY COUNT(i.id) ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return null;
    }
}
