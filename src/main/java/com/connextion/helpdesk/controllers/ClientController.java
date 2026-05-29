package com.connextion.helpdesk.controllers;

import com.connextion.helpdesk.models.Client;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/clients")
public class ClientController {

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

        // TODO: Call Service layer to insert client and client_services into SQL Server
        
        response.put("message", "Client registered successfully");
        return Response.status(Response.Status.CREATED).entity(response).build();
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

        // TODO: Database lookup and validation
        // Mock authentication validation for testing
        if ("client@connection.com".equals(email) && "password123".equals(password)) {
            response.put("message", "Authentication successful");
            response.put("email", email);
            return Response.ok(response).build();
        }

        response.put("error", "Invalid email or password");
        return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
    }
}
