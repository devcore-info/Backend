package com.connextion.helpdesk.services;

import com.connextion.helpdesk.models.Comment;
import com.connextion.helpdesk.models.Issue;
import com.connextion.helpdesk.models.Note;
import com.connextion.helpdesk.models.Bitacora;
import com.connextion.helpdesk.repositories.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ExecutorService executorService;

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

        boolean success = issueRepository.create(issue);
        if (success) {
            final int issueId = issue.getId();
            final String contactEmail = issue.getContactEmail() != null ? issue.getContactEmail() : "CLIENT";
            final String description = issue.getDescription();
            final int serviceId = issue.getServiceId();

            // CONCURRENT PROCESSING (THREADS): Run classification and assignment routing asynchronously
            executorService.submit(() -> {
                try {
                    // 1. Log creation to Bitacora
                    issueRepository.addBitacora(new Bitacora(issueId, contactEmail, "CREACION", 
                            "El cliente reportó la incidencia: " + description));

                    // 2. Perform Automatic Classification based on keywords
                    String classification = "Media";
                    String descLower = description.toLowerCase();
                    if (descLower.contains("urgente") || descLower.contains("caído") || descLower.contains("caido") ||
                        descLower.contains("sin servicio") || descLower.contains("no funciona") || 
                        descLower.contains("crítico") || descLower.contains("critico") || 
                        descLower.contains("bloqueado") || descLower.contains("seguridad")) {
                        classification = "Alta";
                    } else if (descLower.contains("lento") || descLower.contains("lenta") || 
                               descLower.contains("intermitencia") || descLower.contains("intermitente")) {
                        classification = "Media";
                    } else if (descLower.contains("duda") || descLower.contains("pregunta") || 
                               descLower.contains("consulta") || descLower.contains("informacion") || 
                               descLower.contains("información")) {
                        classification = "Baja";
                    }

                    // 3. Log classification event to Bitacora
                    issueRepository.addBitacora(new Bitacora(issueId, "SYSTEM", "CLASIFICACION", 
                            "Clasificación automática determinada como '" + classification + "' basada en análisis de palabras clave."));

                    // 4. Find the best supporter available for this service type (with fewest active cases)
                    Integer bestSupporterId = issueRepository.findBestSupporterForService(serviceId);

                    // 5. Update the Issue and log assignment to Bitacora
                    issueRepository.updateClassificationAndAssignee(issueId, classification, bestSupporterId);

                    if (bestSupporterId != null) {
                        issueRepository.addBitacora(new Bitacora(issueId, "SYSTEM", "ASIGNACION", 
                                "Asignación automática al soportista con ID: " + bestSupporterId + " (mínima carga de trabajo)."));
                    } else {
                        issueRepository.addBitacora(new Bitacora(issueId, "SYSTEM", "ASIGNACION", 
                                "No se encontró soportista disponible en el sistema. Pendiente de asignación manual."));
                    }

                } catch (SQLException e) {
                    System.err.println("Error in concurrent ticket routing: " + e.getMessage());
                }
            });
        }

        return success;
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

        boolean success = issueRepository.addComment(comment);
        if (success) {
            final int issueId = comment.getIssueId();
            final String author = comment.getUserType() + " (ID: " + comment.getUserId() + ")";
            final String desc = comment.getDescription();

            // CONCURRENT PROCESSING (THREADS): Log asynchronously to Bitacora
            executorService.submit(() -> {
                try {
                    issueRepository.addBitacora(new Bitacora(issueId, author, "COMENTARIO", 
                            "Se añadió un comentario público: " + desc));
                } catch (SQLException e) {
                    System.err.println("Error writing comment to bitacora asynchronously: " + e.getMessage());
                }
            });
        }

        return success;
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

    public boolean assignTicket(int id, int supportUserId) throws SQLException, IllegalArgumentException {
        if (id <= 0 || supportUserId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID or support user ID");
        }
        boolean success = issueRepository.assignSupportUser(id, supportUserId);
        if (success) {
            // CONCURRENT PROCESSING (THREADS): Log asynchronously to Bitacora
            executorService.submit(() -> {
                try {
                    issueRepository.addBitacora(new Bitacora(id, "SUPERVISOR", "ASIGNACION", 
                            "Caso asignado o reasignado manualmente al técnico con ID: " + supportUserId));
                } catch (SQLException e) {
                    System.err.println("Error writing assignment to bitacora asynchronously: " + e.getMessage());
                }
            });
        }
        return success;
    }

    public boolean updateTicketStatus(int id, String status, String resolutionComment) throws SQLException, IllegalArgumentException {
        if (id <= 0 || status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID and status are required");
        }
        boolean success = issueRepository.updateStatus(id, status, resolutionComment);
        if (success) {
            // CONCURRENT PROCESSING (THREADS): Log asynchronously to Bitacora
            executorService.submit(() -> {
                try {
                    issueRepository.addBitacora(new Bitacora(id, "SOPORTE", "CAMBIO_ESTADO", 
                            "Estado del caso cambiado a: " + status));
                    if (status.equalsIgnoreCase("Resuelto") && resolutionComment != null && !resolutionComment.trim().isEmpty()) {
                        issueRepository.addBitacora(new Bitacora(id, "SOPORTE", "RESOLUCION", 
                                "Caso resuelto. Diagnóstico final: " + resolutionComment));
                    }
                } catch (SQLException e) {
                    System.err.println("Error writing status update to bitacora asynchronously: " + e.getMessage());
                }
            });
        }
        return success;
    }

    public boolean addNote(Note note) throws SQLException, IllegalArgumentException {
        if (note == null || note.getDescription() == null || note.getDescription().trim().isEmpty() ||
            note.getIssueId() <= 0 || note.getSupportUserId() <= 0) {
            throw new IllegalArgumentException("Description, ticket ID, and support user ID are required");
        }
        boolean success = issueRepository.addNote(note);
        if (success) {
            final int issueId = note.getIssueId();
            final int supportUserId = note.getSupportUserId();
            final String desc = note.getDescription();

            // CONCURRENT PROCESSING (THREADS): Log asynchronously to Bitacora
            executorService.submit(() -> {
                try {
                    issueRepository.addBitacora(new Bitacora(issueId, "SUPPORT (ID: " + supportUserId + ")", "NOTA", 
                            "Se ingresó una nota técnica interna: " + desc));
                } catch (SQLException e) {
                    System.err.println("Error writing note to bitacora asynchronously: " + e.getMessage());
                }
            });
        }
        return success;
    }

    // Get Bitacora Audit logs
    public List<Bitacora> getBitacora(int issueId) throws SQLException {
        if (issueId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID");
        }
        return issueRepository.getBitacoraByIssueId(issueId);
    }
}
