package com.marketdata.config;

import com.marketdata.websocket.TickWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class TickWebSocketConfig implements WebSocketConfigurer {
    private final TickWebSocketHandler tickWebSocketHandler;

    public TickWebSocketConfig(TickWebSocketHandler tickWebSocketHandler) {
        this.tickWebSocketHandler = tickWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(tickWebSocketHandler, "/ws/ticks").setAllowedOrigins("*");
    }
}
