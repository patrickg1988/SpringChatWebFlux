package com.webflux.chat;

import java.time.Instant;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ChatService {

    // multicast = an alle, die gerade verbunden sind
    private final Sinks.Many<ChatMessage> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    public void publish(String user, String text) {
        if (user == null || user.isBlank()) user = "anon";
        if (text == null || text.isBlank()) return;

        sink.tryEmitNext(new ChatMessage(user.trim(), text.trim(), Instant.now()));
    }

    public Flux<ChatMessage> stream() {
        return sink.asFlux();
    }
}

