package com.connextion.helpdesk.controllers;

import com.connextion.helpdesk.models.SupportUser;
import com.connextion.helpdesk.services.SupportUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/support")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SupportController {

    @Autowired
    private SupportUserService supportUserService;

    // CU7: Register Support User (Supporter or Supervisor)
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerSupportUser(@RequestBody SupportUser user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean success = supportUserService.registerSupportUser(user);
            if (success) {
                response.put("message", "Support user registered successfully");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("error", "Failed to register support user");
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

    // CU8: Support / Supervisor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginSupportUser(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        if (credentials == null) {
            response.put("error", "Email and password are required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        String email = credentials.get("email");
        String password = credentials.get("password");

        try {
            SupportUser authenticatedUser = supportUserService.loginSupportUser(email, password);
            if (authenticatedUser != null) {
                response.put("message", "Authentication successful");
                response.put("email", authenticatedUser.getEmail());
                response.put("isSupervisor", authenticatedUser.getIsSupervisor());
                response.put("supportUserId", authenticatedUser.getId());
                response.put("name", authenticatedUser.getName());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("error", "Invalid email/password or user has no services assigned");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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
