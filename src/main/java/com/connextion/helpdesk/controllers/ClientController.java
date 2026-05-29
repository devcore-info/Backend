package com.connextion.helpdesk.controllers;

import com.connextion.helpdesk.models.Client;
import com.connextion.helpdesk.repositories.ClientRepository;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/clients")
public class ClientController {

    private final ClientRepository clientRepository = new ClientRepository();

    // CU1: Register Client User
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerClient(Client client) {
        Map<String, String> response = new HashMap<>();
        
        // Business Rule Validations
        if (client == null || client.getName() == null || client.getFirstSurname() == null || 
            client.getSecondSurname() == null || client.getEmail() == null || 
            client.getPassword() == null || client.getServices() == null || client.getServices().isEmpty()) {
            
            response.put("error", "Missing required fields or services");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        try {
            boolean success = clientRepository.register(client);
            if (success) {
                response.put("message", "Client registered successfully");
                return Response.status(Response.Status.CREATED).entity(response).build();
            } else {
                response.put("error", "Failed to register client");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
        } catch (SQLException e) {
            response.put("error", "Database error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

    // CU2: Client Login
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginClient(Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        
        if (credentials == null) {
            response.put("error", "Email and password are required");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }
        
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            response.put("error", "Email and password are required");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        try {
            Client authenticatedClient = clientRepository.login(email, password);
            if (authenticatedClient != null) {
                response.put("message", "Authentication successful");
                response.put("email", authenticatedClient.getEmail());
                response.put("clientId", authenticatedClient.getId());
                response.put("name", authenticatedClient.getName());
                return Response.ok(response).build();
            } else {
                response.put("error", "Invalid email or password");
                return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
            }
        } catch (SQLException e) {
            response.put("error", "Database error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}
