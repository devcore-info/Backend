package com.connextion.helpdesk.controllers;

import com.connextion.helpdesk.models.Client;
import com.connextion.helpdesk.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ClientController {

    @Autowired
    private ClientService clientService;

    // CU1: Register Client User
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerClient(@RequestBody Client client) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean success = clientService.registerClient(client);
            if (success) {
                response.put("message", "Client registered successfully");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("error", "Failed to register client");
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

    // CU2: Client Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginClient(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        if (credentials == null) {
            response.put("error", "Email and password are required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        String email = credentials.get("email");
        String password = credentials.get("password");

        try {
            Client authenticatedClient = clientService.loginClient(email, password);
            if (authenticatedClient != null) {
                response.put("message", "Authentication successful");
                response.put("email", authenticatedClient.getEmail());
                response.put("clientId", authenticatedClient.getId());
                response.put("name", authenticatedClient.getName());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("error", "Invalid email or password");
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
