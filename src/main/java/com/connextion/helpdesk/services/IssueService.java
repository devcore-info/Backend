package com.connextion.helpdesk.services;

import com.connextion.helpdesk.models.Comment;
import com.connextion.helpdesk.models.Issue;
import com.connextion.helpdesk.models.Note;
import com.connextion.helpdesk.repositories.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    // CU4: Create Ticket
    public boolean createIssue(Issue issue) throws SQLException, IllegalArgumentException {
        if (issue == null || issue.getDescription() == null || issue.getDescription().trim().isEmpty() ||
            issue.getClientId() <= 0 || issue.getServiceId() <= 0) {
            throw new IllegalArgumentException("Description, client ID, and service ID are required");
        }

        // Validate Business Rule: Client must have the selected service contracted
        boolean hasService = issueRepository.hasService(issue.getClientId(), issue.getServiceId());
        if (!hasService) {
            throw new IllegalArgumentException("The selected service is not associated with this client");
        }

        // Complete default data
        issue.setStatus("Ingresado");
        issue.setClassification("Media");

        return issueRepository.create(issue);
    }

    // CU5: Get tickets list for client
    public List<Issue> getIssuesByClient(int clientId) throws SQLException {
        if (clientId <= 0) {
            throw new IllegalArgumentException("Invalid client ID");
        }
        return issueRepository.getByClientId(clientId);
    }

    // CU6 and CU10: Get issue details
    public Issue getIssueDetails(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID");
        }
        return issueRepository.getById(id);
    }

    // CU6: Add Comment
    public boolean addComment(Comment comment) throws SQLException, IllegalArgumentException {
        if (comment == null || comment.getDescription() == null || comment.getDescription().trim().isEmpty() ||
            comment.getIssueId() <= 0 || comment.getUserType() == null || comment.getUserId() <= 0) {
            throw new IllegalArgumentException("Description, ticket ID, user type, and user ID are required");
        }
        
        // Enforce user type validation
        String type = comment.getUserType().toUpperCase();
        if (!type.equals("CLIENT") && !type.equals("SUPPORT")) {
            throw new IllegalArgumentException("Invalid user type. Must be CLIENT or SUPPORT");
        }
        comment.setUserType(type);

        return issueRepository.addComment(comment);
    }

    // CU6: Get Comments
    public List<Comment> getComments(int issueId) throws SQLException {
        if (issueId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID");
        }
        return issueRepository.getCommentsByIssueId(issueId);
    }

    // CU10: List all issues for support
    public List<Issue> getAllIssues() throws SQLException {
        return issueRepository.getAll();
    }

    // Get Notes for an issue
    public List<Note> getNotes(int issueId) throws SQLException {
        if (issueId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID");
        }
        return issueRepository.getNotesByIssueId(issueId);
    }
}
