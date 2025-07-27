package com.marketdata.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class TickWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("🟢 New WebSocket client connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("🔴 WebSocket client disconnected: " + session.getId());
    }

    public void broadcast(String tickJson) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(tickJson));
            } catch (Exception e) {
                System.err.println("❌ Failed to send tick to client: " + session.getId());
            }
        }
    }
}
