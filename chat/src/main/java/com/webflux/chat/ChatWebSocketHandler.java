package com.webflux.chat;
// Package für alle WebSocket- und Chat-Komponenten

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
// Jackson 3: Bibliothek zum Umwandeln zwischen JSON und Java-Objekten

/**
 * ChatWebSocketHandler
 *
 * Diese Klasse verbindet:
 * - WebSocket-Verbindungen (Client ↔ Server)
 * - ChatService (Geschäftslogik)
 *
 * Sie verarbeitet eingehende Nachrichten vom Client
 * und sendet neue Chat-Nachrichten an den Client zurück.
 */
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    // Service mit der eigentlichen Chat-Logik
    private final ChatService chatService;

    // JSON-Mapper zum Lesen und Schreiben von JSON-Daten
    private final JsonMapper json; // Jackson 3 Bean

    /**
     * Konstruktor
     *
     * Spring übergibt automatisch:
     * - den ChatService
     * - den JsonMapper
     * (Dependency Injection)
     */
    public ChatWebSocketHandler(ChatService chatService, JsonMapper json) {
        this.chatService = chatService;
        this.json = json;
    }

    /**
     * handle(...)
     *
     * Diese Methode wird aufgerufen, sobald ein Client
     * eine WebSocket-Verbindung herstellt.
     *
     * Sie behandelt:
     * - eingehende Nachrichten (inbound)
     * - ausgehende Nachrichten (outbound)
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {

        /**
         * INBOUND:
         * Nachrichten vom Client → Server
         */
        Mono<Void> inbound = session.receive()
                // WebSocketMessage → Textinhalt (JSON-String)
                .map(WebSocketMessage::getPayloadAsText)

                // Verarbeitung der empfangenen Nachricht
                .doOnNext(payload -> {
                    try {
                        // JSON-Text in einen Baum (JsonNode) einlesen
                        var node = json.readTree(payload);

                        // Übergabe der Daten an den ChatService
                        chatService.publish(
                                node.path("user").asText("anon"),
                                node.path("text").asText("")
                        );

                    } catch (JacksonException ignored) {
                        // Ungültiges JSON wird bewusst ignoriert,
                        // damit die WebSocket-Verbindung stabil bleibt
                    }
                })

                // Wir senden nichts direkt zurück → Abschluss mit then()
                .then();

        /**
         * OUTBOUND:
         * Nachrichten vom Server → Client
         */
        Mono<Void> outbound = session.send(
                chatService.stream()

                        // ChatMessage → JSON → WebSocketMessage
                        .map(msg -> {
                            try {
                                return session.textMessage(
                                        json.writeValueAsString(msg)
                                );
                            } catch (JacksonException e) {
                                // Fallback-Nachricht bei Serialisierungsfehler
                                return session.textMessage(
                                        "{\"user\":\"system\",\"text\":\"serialize error\",\"ts\":\"1970-01-01T00:00:00Z\"}"
                                );
                            }
                        })
        );

        /**
         * Mono.when(...)
         *
         * Startet inbound und outbound gleichzeitig.
         * Die Verbindung bleibt offen, solange beide aktiv sind.
         */
        return Mono.when(inbound, outbound);
    }
}
