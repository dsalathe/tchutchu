# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TchuTchu is a Scala 3 implementation of the board game "Tickets to Ride" (Swiss variant). It's a full-stack web application with a Spring Boot backend, Vue.js 3 frontend, and real-time WebSocket multiplayer support.

## Build & Run Commands

```bash
# Full build (Maven + Docker image + Helm chart)
make all

# Maven build with keystore generation
make dist

# Run tests (preferred method)
sbt test

# Build fat JAR
sbt assembly

# Run the application
java -jar target/tchutchu.jar
# Or via Docker:
make run

# Frontend development (from src/frontend/)
npm install
npm run serve    # Dev server with hot-reload
npm run build    # Production build
npm run lint     # ESLint check
```

## Architecture

**Communication Flow:**
```
Vue 3 Client ↔ WebSocket (STOMP/SockJS) ↔ TchuTchuController ↔ Game Engine ↔ RemotePlayerProxyWS ↔ Client
```

**Key Backend Packages** (`src/main/scala/ch/coachdave/tchutchu/`):
- `controller/` - Spring controllers; `TchuTchuController.scala` is the main WebSocket message handler
- `game/` - Core game logic (22 files): `Game.scala` orchestrates turns, `GameState.scala` manages state machine, `ChMap.scala` defines the Swiss map
- `net/` - Serialization layer using custom binary protocol with Base64 encoding and delimiters (`;`, `:`, `,`)
- `model/` - WebSocket message types

**Frontend** (`src/frontend/src/`):
- `store/index.js` - Vuex state management
- `components/BoardGame.vue` - Main game board
- `components/GameSetup.vue` - Game creation/joining

**State Management:**
- Immutable-style GameState with functional transformations
- Separates `PublicGameState` (visible to all) from private `PlayerState`
- Cache-based game tracking with 24-hour TTL

## Testing

```bash
sbt test  # Preferred - runs ScalaTest suite
```

Tests cover: game entities (Card, Route, Ticket, Station), state transitions, serialization (Serdes), and controllers. Located in `src/test/scala/`.

## WebSocket Protocol

- STOMP over SockJS with fallbacks
- Per-user queue: `/queue/tchu-events`
- Broadcast topic: `/topic/tchu-events`
- Application prefix: `/app`

## Key Files for Understanding the Codebase

1. `TchuTchuApp.scala` - Spring Boot entry point
2. `controller/TchuTchuController.scala` - Game command orchestration
3. `game/Game.scala` - Turn management and game flow
4. `game/GameState.scala` - State machine with functional transformations
5. `game/ChMap.scala` - Swiss map definition (stations, routes, tickets)
6. `net/Serdes.scala` - Serialization/deserialization framework
