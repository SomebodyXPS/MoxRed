# MoxRed — AI Server Staff Operator for Minecraft

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.11-green.svg)](https://papermc.io)
[![Java Version](https://img.shields.io/badge/Java-17%2F21%2F25-orange.svg)](https://adoptium.net)
[![Version](https://img.shields.io/badge/version-beta--1.0.0-purple.svg)](https://github.com/SomebodyXPS/MoxRed/releases)
[![Discord](https://img.shields.io/badge/Discord-Join%20Server-5865F2?logo=discord&logoColor=white)](https://discord.gg)

> **MoxRed** is an advanced, AI-powered **Server Staff Operator** designed for Minecraft servers (Spigot/Paper). It allows administrators and staff to manage, audit, and automate Minecraft servers using natural language commands directly through Discord — powered by leading AI models (Google Gemini, OpenAI, Claude, and more).

Created and developed by **[SomebodyXPS](https://github.com/SomebodyXPS)**. MoxRed bridges the gap between complex server administration and conversational interfaces. It runs safely, securely, and deterministically inside your server runtime — with zero general terminal/OS-level exposure.

---

## 📖 Table of Contents

- [✨ Key Features](#-key-features)
- [🛠 Project Architecture](#-project-architecture)
- [🚀 Getting Started](#-getting-started)
- [📚 Documentation](#-documentation)
- [🧠 Supported AI Models](#-supported-ai-models)
- [🔒 Safety & Approval Workflow](#-safety--approval-workflow)
- [⚙️ Building from Source](#️-building-from-source)
- [🤝 Contributing](#-contributing)
- [📄 License & Trademarks](#-license--trademarks)

---

## ✨ Key Features

- **🗣️ Natural Language Control**: No more memorizing command flags or digging through config syntax. Simply ask MoxRed: *"Kick Steve for griefing"*, *"Give 64 diamonds to Emma"*, or *"Broadcast that PvP is now disabled"*.
- **🔍 Multi-Plugin Discovery**: Dynamically scans and indexes active plugins (Vault, Essentials, LuckPerms, ViaVersion, ProtocolLib, etc.) to query balances, edit configs, and check statistics on the fly.
- **📊 Real-Time Server Monitoring**: Ask about TPS, lag, memory usage, and player counts — all through natural language.
- **🛡️ Safety-First Approval Workflow**: High-risk actions (bans, config edits, item distribution) automatically trigger an in-Discord approval prompt with **Approve/Deny** buttons. Only admins with the designated role can execute.
- **🧠 AI-Powered Reasoning**: MoxRed's AI service plans server actions, evaluates potential risks, and runs dry-run verifications before execution.
- **🔗 Secure WebSocket Communication**: The plugin and bot communicate over a secure local-only WebSocket — no external exposure.
- **⚙️ Deterministic Execution**: Actions are executed safely inside the server thread context, ensuring consistency and reliability.

---

## 🛠 Project Architecture

MoxRed is a multi-module Maven project consisting of two core components communicating over a secure local WebSocket connection:

### 1. `moxred-core` (Minecraft Plugin)
- Runs directly inside your Spigot or Paper server.
- Performs dynamic plugin discovery scans to register and index server capabilities.
- Exposes a secure, local-only WebSocket server.
- Safely executes approved staff actions (player commands, teleports, item giving, config changes) inside the server thread context.

### 2. `moxred-bot` (Discord Bot)
- Connects to your Discord server using the Java Discord API (JDA).
- Interfaces with `moxred-core` via WebSocket.
- Houses the **Advanced AI Service** which processes natural language requests, plans actions, evaluates risks, and runs dry-run verifications.
- Handles user permissions and prompts Discord administrators for manual approval on high-risk actions.

---

## 🚀 Getting Started

Setting up MoxRed takes just a few minutes! See the full guide:

👉 **[Getting Started Guide →](docs/GETTING_STARTED.md)**

### Quick Prerequisites
- Minecraft server running **Spigot** or **Paper** (version **1.21.11** recommended)
- **Java 17+** (Java 21 or 25 recommended)
- A Discord account with bot creation permissions
- An AI provider API key (Google Gemini, OpenAI, Claude, or OpenRouter)

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [📘 Getting Started](docs/GETTING_STARTED.md) | Step-by-step setup guide for both the Minecraft plugin and Discord bot |
| [📗 Features & Usage](docs/FEATURES_AND_USAGE.md) | Detailed feature descriptions, usage examples, and custom automations |
| [📙 Supported AI Models](docs/SUPPORTED_MODELS.md) | Full list of supported AI providers and configuration instructions |
| [⚖️ Trademarks](TRADEMARKS.md) | MoxRed trademark policy and usage guidelines |

---

## 🧠 Supported AI Models

MoxRed is model-agnostic and supports multiple AI providers:

| Provider | Recommended Models | Config Key |
|----------|-------------------|------------|
| **Google Gemini** ⭐ | `gemini-2.5-pro`, `gemini-2.5-flash` | `gemini` |
| **OpenAI** | `gpt-4o`, `gpt-4o-mini` | `openai` |
| **Anthropic Claude** | `claude-3.7-sonnet`, `claude-3.5-sonnet` | `claude` |
| **OpenRouter** | `deepseek-chat`, `llama-3.3-70b`, `qwen2.5-72b` | `openrouter` |

See **[Supported Models →](docs/SUPPORTED_MODELS.md)** for full configuration details.

---

## 🔒 Safety & Approval Workflow

MoxRed is designed with built-in security constraints to prevent abuse:

| Action Type | Examples | Approval Required |
|-------------|----------|-------------------|
| ✅ **Safe** | Read stats, list players, check TPS, get help | No — executed instantly |
| ⚠️ **High-Risk** | Bans, config changes, item distribution, scripts | Yes — Discord admin approval via buttons |

**Approval Flow:**
1. Bot formats the requested command
2. Posts a rich embed with **Approve** and **Deny** buttons
3. Only users with the designated Admin/Owner Discord role can execute

---

## ⚙️ Building from Source

MoxRed is a Maven multi-module project. To build from source:

```bash
# Clone the repository
git clone https://github.com/SomebodyXPS/MoxRed.git
cd MoxRed

# Build all modules
mvn clean package
```

The built JARs will be available in each module's `target/` directory.

---

## 🤝 Contributing

Contributions are welcome! Please see the [CONTRIBUTING.md](CONTRIBUTING.md) guide for details on our code of conduct and the process for submitting pull requests.

---

## 📄 License & Trademarks

- **Source Code**: Licensed under the [Apache License 2.0](LICENSE).
- **Name, Logo & Branding**: Protected under the [MoxRed Trademark Policy](TRADEMARKS.md).

---

<div align="center">

**Made with ❤️ by [SomebodyXPS](https://github.com/SomebodyXPS)**

[![GitHub Repo Stars](https://img.shields.io/github/stars/SomebodyXPS/MoxRed?style=social)](https://github.com/SomebodyXPS/MoxRed)
[![GitHub Forks](https://img.shields.io/github/forks/SomebodyXPS/MoxRed?style=social)](https://github.com/SomebodyXPS/MoxRed/fork)

</div>
