package com.webflux.chat;

import java.time.Instant;

public record ChatMessage(String user, String text, Instant ts) {}
