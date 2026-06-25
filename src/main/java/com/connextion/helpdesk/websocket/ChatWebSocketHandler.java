package com.connextion.helpdesk.websocket;

import com.connextion.helpdesk.models.Comment;
import com.connextion.helpdesk.services.IssueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private IssueService issueService;

    // Map: ticketId -> list of active WebSocket sessions
    private static final Map<Integer, List<WebSocketSession>> ticketSessions = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String[] params = getPathParameters(session);
        if (params == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        try {
            int ticketId = Integer.parseInt(params[0]);
            ticketSessions.computeIfAbsent(ticketId, k -> Collections.synchronizedList(new ArrayList<>())).add(session);
        } catch (NumberFormatException e) {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String[] params = getPathParameters(session);
        if (params == null) {
            return;
        }

        int ticketId = Integer.parseInt(params[0]);
        String userType = params[1].toUpperCase();
        int userId = Integer.parseInt(params[2]);

        String payload = message.getPayload();
        // Parse incoming message JSON
        Map<String, String> msgMap = objectMapper.readValue(payload, Map.class);
        String text = msgMap.get("description");

        if (text == null || text.trim().isEmpty()) {
            return;
        }

        // 1. Create and save comment to Database
        Comment comment = new Comment();
        comment.setDescription(text);
        comment.setIssueId(ticketId);
        comment.setUserType(userType);
        comment.setUserId(userId);

        try {
            boolean saved = issueService.addComment(comment);
            if (saved) {
                // Fetch updated comments list or construct the message for broadcasting
                // We fetch the newly saved comment to ensure we have the userName populated correctly from DB
                List<Comment> updatedComments = issueService.getComments(ticketId);
                // Find our comment in the list or get the last one
                Comment savedComment = updatedComments.stream()
                        .filter(c -> c.getId() == comment.getId())
                        .findFirst()
                        .orElse(comment);

                // 2. Broadcast the message to all clients in the same ticket session
                String jsonResponse = objectMapper.writeValueAsString(savedComment);
                TextMessage broadcastMessage = new TextMessage(jsonResponse);

                List<WebSocketSession> sessions = ticketSessions.get(ticketId);
                if (sessions != null) {
                    synchronized (sessions) {
                        for (WebSocketSession s : sessions) {
                            if (s.isOpen()) {
                                s.sendMessage(broadcastMessage);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error saving websocket comment: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String[] params = getPathParameters(session);
        if (params != null) {
            try {
                int ticketId = Integer.parseInt(params[0]);
                List<WebSocketSession> sessions = ticketSessions.get(ticketId);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        ticketSessions.remove(ticketId);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private String[] getPathParameters(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String path = uri.getPath();
        String[] parts = path.split("/");
        // Map path: /chat-socket/{ticketId}/{userType}/{userId}
        // parts[0] = ""
        // parts[1] = "chat-socket"
        // parts[2] = {ticketId}
        // parts[3] = {userType}
        // parts[4] = {userId}
        if (parts.length >= 5) {
            return new String[]{parts[2], parts[3], parts[4]};
        }
        return null;
    }
}
