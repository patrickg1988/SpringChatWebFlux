# SpringChatWebFlux

Ein **minimaler Gruppenchat** auf Basis von **Spring Boot** und **Spring WebFlux** mit **WebSockets**.  
Clients verbinden sich per WebSocket mit dem Server und erhalten neue Chat-Nachrichten als Stream in Echtzeit.

---

## Features

- **WebSocket-Endpoint**: `/ws/chat`
- **Broadcast an alle verbundenen Clients** (Multicast)
- Automatisches **Buffering bei langsamen Clients**
- Nachrichtenformat als **Java Record**  
  `ChatMessage(user, text, ts)` mit UTC-Timestamp
- Sehr kleine, übersichtliche Codebasis  
  → ideal als **Lern-, Demo- oder Unterrichtsprojekt**

---

## Tech-Stack

- **Java 17**
- **Spring Boot 4.0.2**
- **Spring WebFlux** (reaktiv)
- **Project Reactor** (`Flux`, `Sinks`)
- Maven

---

## Projektstruktur

Die eigentliche Spring-Boot-Anwendung befindet sich im Unterordner `chat/`.

Wichtige Klassen:

- `ChatApplication`  
  Einstiegspunkt der Spring-Boot-Anwendung

- `WebSocketConfig`  
  Konfiguriert das WebSocket-Mapping (`/ws/chat`) und den `WebSocketHandlerAdapter`

- `ChatWebSocketHandler`  
  Nimmt JSON-Nachrichten vom Client entgegen und sendet JSON zurück

- `ChatService`  
  Verteilt Nachrichten reaktiv an alle verbundenen Clients (Multicast-Sink)

- `ChatMessage`  
  Datenobjekt (DTO) als Java `record`

---

## Quickstart

### 1️⃣ Anwendung starten

```bash
cd chat
./mvnw spring-boot:run
# oder:
mvn spring-boot:run
