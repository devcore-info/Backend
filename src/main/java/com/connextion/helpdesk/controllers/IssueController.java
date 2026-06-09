package com.connextion.helpdesk.controllers;

import com.connextion.helpdesk.models.Comment;
import com.connextion.helpdesk.models.Issue;
import com.connextion.helpdesk.models.Note;
import com.connextion.helpdesk.services.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/issues")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class IssueController {

    @Autowired
    private IssueService issueService;

    // CU4: Create Ticket (Ingresar solicitud)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createIssue(@RequestBody Issue issue) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = issueService.createIssue(issue);
            if (success) {
                response.put("message", "Ticket created successfully");
                response.put("issueId", issue.getId());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("error", "Failed to create ticket");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            response.put("error", "Database error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CU5: Get ticket list for Client
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Object> getIssuesByClient(@PathVariable int clientId) {
        try {
            List<Issue> issues = issueService.getIssuesByClient(clientId);
            return new ResponseEntity<>(issues, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Database error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CU6 and CU10: Get ticket details, comments, and notes
    @GetMapping("/{id}")
    public ResponseEntity<Object> getIssueDetails(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Issue issue = issueService.getIssueDetails(id);
            if (issue == null) {
                response.put("error", "Ticket not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            List<Comment> comments = issueService.getComments(id);
            List<Note> notes = issueService.getNotes(id);

            response.put("issue", issue);
            response.put("comments", comments);
            response.put("notes", notes);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            response.put("error", "Database error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CU10: Get all tickets for support staff (technical/supervisor panel)
    @GetMapping
    public ResponseEntity<Object> getAllIssues() {
        try {
            List<Issue> issues = issueService.getAllIssues();
            return new ResponseEntity<>(issues, HttpStatus.OK);
        } catch (SQLException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Database error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CU6: Add Comment to Ticket
    @PostMapping("/{id}/comments")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable int id, @RequestBody Comment comment) {
        Map<String, Object> response = new HashMap<>();
        try {
            comment.setIssueId(id);
            boolean success = issueService.addComment(comment);
            if (success) {
                response.put("message", "Comment added successfully");
                response.put("commentId", comment.getId());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("error", "Failed to add comment");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            response.put("error", "Database error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
