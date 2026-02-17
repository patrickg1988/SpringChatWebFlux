package com.webflux.chat;
// Package für alle Klassen, die zur Chat-Funktionalität gehören

import java.time.Instant;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
// Klassen aus Project Reactor für reaktive, asynchrone Datenströme

/**
 * ChatService
 *
 * Diese Klasse stellt die zentrale Logik für den Chat bereit.
 * Sie empfängt neue Nachrichten und stellt sie allen verbundenen Clients
 * als Datenstrom (Stream) zur Verfügung.
 */
@Service
public class ChatService {

    /**
     * Sinks.Many ist eine Art "Verteiler".
     *
     * - multicast(): Jede neue Nachricht wird an ALLE aktuell
     *   verbundenen Abonnenten weitergegeben
     * - onBackpressureBuffer(): Falls ein Client zu langsam ist,
     *   werden Nachrichten zwischengespeichert
     *
     * Vergleich:
     * Sink ≈ Lautsprecher
     * Clients ≈ Zuhörer
     */
    private final Sinks.Many<ChatMessage> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    /**
     * publish(...)
     *
     * Diese Methode wird aufgerufen, wenn ein Benutzer eine neue
     * Chat-Nachricht sendet.
     */
    public void publish(String user, String text) {

        // Falls kein Benutzername angegeben wurde → Standardname setzen
        if (user == null || user.isBlank()) user = "anon";

        // Leere oder fehlende Nachrichten werden nicht weitergeleitet
        if (text == null || text.isBlank()) return;

        // Erzeugen einer neuen ChatMessage (Record)
        // - Benutzername und Text werden bereinigt (trim)
        // - Zeitstempel wird beim Senden gesetzt
        sink.tryEmitNext(
                new ChatMessage(
                        user.trim(),
                        text.trim(),
                        Instant.now()
                )
        );
    }

    /**
     * stream()
     *
     * Liefert einen reaktiven Datenstrom (Flux) aller Chat-Nachrichten.
     *
     * Jeder Client, der diesen Stream abonniert, erhält automatisch
     * alle neuen Nachrichten in Echtzeit.
     */
    public Flux<ChatMessage> stream() {
        return sink.asFlux();
    }
}
