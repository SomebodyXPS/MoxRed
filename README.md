# 🔴 MoxRed

**Enterprise-Grade AI Control System for Minecraft Servers**  
**Author:** SomebodyXPS  
**Build System:** Apache Maven

---

MoxRed is a secure, scalable, AI-powered operational control system for Minecraft servers. It enables administrators to manage, monitor, and automate server operations through Discord using advanced AI intelligence.

MoxRed follows strict architectural separation between execution and intelligence to guarantee maximum security and production stability.

---

# 🏗 Architecture Overview

MoxRed consists of two independent Maven-based projects:

- **MoxRed-Core** – Secure execution engine  
- **MoxRed-Bot** – AI intelligence & Discord interface  

```
Discord (Admin Only)
↓
MoxRed-Bot (Gemini 2.5 Flash)
↓ Direct Plugin Access
MoxRed-Core (Execution Layer)
↓
Minecraft Server
```

AI never directly executes commands inside the Minecraft server.

---

# 📦 Project Structure (Maven Multi-Module)

```
moxred/
│
├── pom.xml                  (Parent POM)
│
├── moxred-core/             (Execution Engine)
│   ├── pom.xml
│   └── src/main/java/...
│
└── moxred-bot/              (Discord AI Bot)
    ├── pom.xml
    └── src/main/java/...
```

---

# 🔴 MoxRed-Core

Deterministic execution engine running inside PaperMC.

## Responsibilities

- Secure WebSocket server (WSS)
- HMAC-SHA256 packet validation
- Timestamp enforcement
- Nonce replay protection
- Strict action whitelist (precompiled actions only)
- Async audit logging
- Telemetry reporting (TPS, memory, players, chunks)
- Workflow execution
- Scheduler & delayed tasks

## Runtime Requirements

- PaperMC 1.20.x+
- Java 17 (LTS)

## Maven Build

### Key Dependencies

- `paper-api` (provided scope)
- `Java-WebSocket`
- `Jackson Databind`
- `Commons Codec` (optional)

### Build

```
mvn clean package
```

Output:
```
moxred-core/target/MoxRed-Core-1.0.0.jar
```

Deploy the JAR into:

```
/plugins/
```

---

# 🤖 MoxRed-Bot

AI-powered control layer running independently from Minecraft.

## Responsibilities

- Discord integration
- Admin role validation
- Gemini 2.5 Flash AI processing
- Natural language interpretation
- Intent-to-action mapping
- Multi-step planning
- Packet signing (HMAC-SHA256)
- Secure WebSocket communication
- Response formatting
- Multi-server orchestration support

## Runtime Requirements

- Java 17+
- Discord Bot Token
- Gemini API access
- Shared secret (must match plugin)

## Maven Dependencies (Typical)

- Discord API library (e.g., JDA)
- Jackson
- WebSocket client
- Logging framework (SLF4J + Logback recommended)

## Build

```
mvn clean package
```

Output:
```
moxred-bot/target/MoxRed-Bot-1.0.0.jar
```

Run with:

```
java -jar moxred-bot-1.0.0.jar
```

---

# 🔐 Security Model

MoxRed uses enterprise-grade safeguards:

- Signed JSON protocol
- HMAC-SHA256 validation
- Timestamp window enforcement
- Nonce replay protection
- Strict action whitelist
- Discord role-based access control
- Async structured audit logging
- Packet size limits
- Workflow step limits
- Rate limiting

If validation fails, execution is rejected immediately.

The plugin remains secure even if the bot layer is compromised.

---

# ⚙ Configuration Overview

## Plugin (`config.yml`)

- WebSocket host/port
- Shared secret
- Timestamp tolerance
- Nonce cache size
- Workflow enable flag
- Raw console flag (disabled by default)

## Bot (`config.yml`)

- Discord token
- Admin role ID
- Gemini API key
- Target server connection info
- Shared secret
- Protocol version

---

# 📈 Scalability

MoxRed supports:

- Single-server setups
- Multi-server orchestration
- Centralized AI control
- Enterprise infrastructure scaling
- Future SaaS model expansion

The bot can manage multiple Minecraft servers through independent secure connections.

---

# 🔄 Versioning Strategy (Maven + Semantic Versioning)

```
MAJOR.MINOR.PATCH
```

Examples:

- `1.0.0` → Initial release
- `1.1.0` → New safe actions
- `1.1.1` → Bug fixes
- `2.0.0` → Protocol changes

Protocol compatibility is maintained within the same major version.

---

# 🧠 Architectural Philosophy

MoxRed enforces separation of responsibility:

- **Execution Layer (MoxRed-Core)** → Deterministic & secure
- **Intelligence Layer (MoxRed-Bot)** → Adaptive & AI-driven
- **Interface Layer (Discord)** → Controlled access

The execution engine never learns or modifies itself.

The AI layer evolves independently.

---

# 🚀 Deployment Strategy

Recommended Environment:

- Ubuntu 22.04 LTS
- Java 17 LTS
- PaperMC 1.20.x
- Reverse proxy for secure WSS (optional)
- Docker or Pterodactyl compatible

---

# 🔮 Roadmap

- Multi-server cluster orchestration
- Predictive TPS mitigation
- AI-driven automated event systems
- Enterprise monitoring dashboard
- SaaS orchestration layer

---

# 📄 License

Private / Custom License (Define as required)

---

# 📌 Final Summary

MoxRed is not simply a Discord bot.

It is a Maven-based, enterprise-structured AI control framework for Minecraft servers.

**MoxRed-Core → Stable Execution Engine**  
**MoxRed-Bot → Intelligent AI Control Layer**

Together they form a scalable, secure, production-grade operational system.
