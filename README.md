# MoxRed — AI Server Staff Operator for Minecraft

[![GitHub License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.11-green.svg)](https://papermc.io)
[![Java Version](https://img.shields.io/badge/Java-17%2F21%2F25-orange.svg)](https://adoptium.net)
[![Version](https://img.shields.io/badge/version-beta--1.0.0-purple.svg)]()

MoxRed is an advanced, AI-powered **Server Staff Operator** specifically designed for Minecraft servers (Spigot/Paper). It allows administrators and staff to manage, audit, and automate Minecraft servers using natural language commands directly through Discord, powered by leading AI models (such as Google Gemini, OpenAI, Claude, and more).

Created and developed by **SomebodyXPS**, MoxRed bridges the gap between complex server administrations and conversational interfaces. It runs safely, securely, and deterministically right inside your server runtime—allowing zero general terminal/OS-level exposure.

---

## 🛠 Project Architecture

MoxRed is a multi-module Maven project consisting of two core components that communicate over a secure local WebSocket connection:

1. **`moxred-core` (Minecraft Plugin)**:
   * Runs directly inside your Spigot or Paper server.
   * Performs dynamic plugin discovery scans to register and index server capabilities.
   * Exposes a secure, local-only WebSocket server.
   * Safely executes approved staff actions (like player commands, teleports, item giving, and configuration changes) inside the server thread context.

2. **`moxred-bot` (Discord Bot)**:
   * Connects to your Discord server using the Java Discord API (JDA).
   * Interfaces with `moxred-core` via WebSocket.
   * Houses the **Advanced AI Service** which processes natural language requests, plans server actions, evaluates potential risks, and runs dry-run verifications.
   * Handles user permissions and prompts Discord administrators for manual approval on high-risk actions (such as bans or configuration edits).

---

## ✨ Key Features

* **Natural Language Control**: Say goodbye to memorizing command flags or searching through config syntax. Ask MoxRed to execute actions like `"Kick Steve for griefing"`, `"Give 64 diamonds to Emma"`, or `"Broadcast that PvP is now disabled"`.
* **Execution Verification & Dry-runs**: MoxRed's AI reasoning engine plans actions, previews potential outcomes, and checks execution safety before applying changes.
* **Knowledge Manager**: Dynamically reads and registers your server's context, including active players, Vault economy stats, placeholder expansions (via PlaceholderAPI), and other loaded plugins.
* **Deterministic Core Action Guard**: High-risk actions automatically require a confirmation flow from authorized server administrators.
* **No Direct Shell Exposure**: Unlike dangerous terminal-level integrations, MoxRed communicates solely through official Bukkit/Paper API wrappers and Minecraft plugins. No bash or OS command execution is allowed.

---

## 📂 Documentation

To get MoxRed up and running or to explore its capabilities, refer to our detailed documentation guides:

* 🚀 [**Getting Started & Installation Guide**](docs/GETTING_STARTED.md) — Step-by-step instructions on setting up `moxred-core` and `moxred-bot`.
* 📖 [**Features & Usage Guide**](docs/FEATURES_AND_USAGE.md) — Learn how to interact with MoxRed and what workflows can be built.
* 🧠 [**Supported AI Models & Setup**](docs/SUPPORTED_MODELS.md) — Configuration guidelines for OpenAI, Google Gemini, Anthropic Claude, and OpenRouter.

---

## 🏗 Building from Source

To build MoxRed yourself, ensure you have **Java 17+ (or Java 21/25)** and **Maven** installed.

1. Clone the repository:
   ```bash
   git clone https://github.com/SomebodyXPS/MoxRed.git
   cd MoxRed
   ```

2. Build the parent project and all modules:
   ```bash
   mvn clean package
   ```

3. The compiled `.jar` binaries will be located under their respective target directories:
   * **Plugin**: `moxred-core/target/MoxRed-Core-beta-1.0.0.jar`
   * **Discord Bot**: `moxred-bot/target/MoxRed-Bot-beta-1.0.0.jar`

---

## 📝 License

This project is open-source and licensed under the [MIT License](LICENSE). 

Developed with ❤️ by **[SomebodyXPS](https://github.com/SomebodyXPS)**.
