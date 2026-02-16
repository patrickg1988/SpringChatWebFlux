package com.webflux.chat;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final JsonMapper json; // Jackson 3 Bean

    public ChatWebSocketHandler(ChatService chatService, JsonMapper json) {
        this.chatService = chatService;
        this.json = json;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(payload -> {
                    try {
                        var node = json.readTree(payload);
                        chatService.publish(
                                node.path("user").asText("anon"),
                                node.path("text").asText("")
                        );
                    } catch (Exception ignored) { }
                })
                .then();

        Mono<Void> outbound = session.send(
                chatService.stream()
                        .map(msg -> {
                            try {
                                return session.textMessage(json.writeValueAsString(msg));
                            } catch (Exception e) {
                                return session.textMessage(
                                        "{\"user\":\"system\",\"text\":\"serialize error\",\"ts\":\"1970-01-01T00:00:00Z\"}"
                                );
                            }
                        })
        );

        return Mono.when(inbound, outbound);
    }
}
