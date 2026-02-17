package com.webflux.chat;

import java.time.Instant;
// Instant repräsentiert einen exakten Zeitpunkt (Datum + Uhrzeit) in UTC.
// Geeignet für Zeitstempel in verteilten Systemen (z. B. Server, Clients, APIs).

/**
 * ChatMessage ist ein sogenannter "Record".
 *
 * Records werden in Java verwendet, um einfache Datenobjekte (DTOs) zu definieren.
 * Sie speichern Daten, enthalten aber keine komplexe Logik.
 *
 * Typische Einsatzfälle:
 * - Übertragung von Daten (z. B. vom Server zum Client)
 * - Nachrichtenobjekte
 * - Ergebnisobjekte
 */
public record ChatMessage(
        String user,   // Name oder Kennung des Absenders der Nachricht
        String text,   // Inhalt der Chat-Nachricht
        Instant ts     // Zeitstempel: Wann wurde die Nachricht erstellt?
) {
    // Der Compiler erzeugt automatisch:
    // - einen Konstruktor mit allen Attributen
    // - Getter-Methoden: user(), text(), ts()
    // - toString(), equals() und hashCode()
    
    // Wichtig:
    // - Records sind unveränderlich (immutable)
    // - Nach dem Erstellen kann der Inhalt nicht mehr geändert werden
}
