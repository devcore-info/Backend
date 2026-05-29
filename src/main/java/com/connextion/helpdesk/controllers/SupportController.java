package com.connextion.helpdesk.controllers;

import com.connextion.helpdesk.models.SupportUser;
import com.connextion.helpdesk.repositories.SupportUserRepository;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/support")
public class SupportController {

    private final SupportUserRepository supportUserRepository = new SupportUserRepository();

    // CU7: Register Support User (Supporter or Supervisor)
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSupportUser(SupportUser user) {
        Map<String, String> response = new HashMap<>();

        // Business Rule Validations
        if (user == null || user.getName() == null || user.getFirstSurname() == null || 
            user.getSecondSurname() == null || user.getEmail() == null || 
            user.getPassword() == null || user.getServices() == null || user.getServices().isEmpty()) {
            
            response.put("error", "Missing required fields or services");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        try {
            boolean success = supportUserRepository.register(user);
            if (success) {
                response.put("message", "Support user registered successfully");
                return Response.status(Response.Status.CREATED).entity(response).build();
            } else {
                response.put("error", "Failed to register support user");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
        } catch (SQLException e) {
            response.put("error", "Database error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

    // CU8: Support / Supervisor Login
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginSupportUser(Map<String, String> credentials) {
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
            SupportUser authenticatedUser = supportUserRepository.login(email, password);
            if (authenticatedUser != null) {
                response.put("message", "Authentication successful");
                response.put("email", authenticatedUser.getEmail());
                response.put("isSupervisor", authenticatedUser.getIsSupervisor());
                response.put("supportUserId", authenticatedUser.getId());
                response.put("name", authenticatedUser.getName());
                return Response.ok(response).build();
            } else {
                response.put("error", "Invalid email/password or user has no services assigned");
                return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
            }
        } catch (SQLException e) {
            response.put("error", "Database error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}
