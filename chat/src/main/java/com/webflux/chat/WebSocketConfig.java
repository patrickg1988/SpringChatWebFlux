package com.webflux.chat;


import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
public class WebSocketConfig {

    @Bean
    HandlerMapping webSocketMapping(ChatWebSocketHandler handler) {
        return new SimpleUrlHandlerMapping(Map.of("/ws/chat", handler), 1);
    }

    @Bean
    WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}

