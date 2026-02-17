package com.webflux.chat;
// Package für die WebSocket-Konfiguration des Chat-Systems

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
// Adapter, der WebSocketHandler in Spring WebFlux lauffähig macht

/**
 * WebSocketConfig
 *
 * Diese Klasse konfiguriert, unter welcher URL
 * der WebSocket-Chat erreichbar ist.
 *
 * Sie enthält KEINE Logik, sondern nur Konfiguration.
 */
@Configuration
public class WebSocketConfig {

    /**
     * webSocketMapping(...)
     *
     * Ordnet eine URL einem WebSocketHandler zu.
     *
     * Beispiel:
     * ws://localhost:8080/ws/chat
     */
    @Bean
    HandlerMapping webSocketMapping(ChatWebSocketHandler handler) {

        // Map:
        // Schlüssel  → URL-Pfad
        // Wert       → zuständiger WebSocketHandler
        return new SimpleUrlHandlerMapping(
                Map.of("/ws/chat", handler),
                1   // Priorität: niedrigere Zahl = höhere Priorität
        );
    }

    /**
     * handlerAdapter()
     *
     * Dieser Adapter ist notwendig, damit Spring WebFlux
     * WebSocketHandler korrekt verarbeiten kann.
     *
     * Ohne diesen Bean:
     * WebSockets funktionieren nicht
     */
    @Bean
    WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
