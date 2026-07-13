# Features & Usage Guide

MoxRed is an open-source AI Server Staff Operator developed by **SomebodyXPS**. Instead of just parsing commands, MoxRed serves as an intelligent assistant that understands your server's entire runtime environment and helps you manage it using natural language.

---

## 💬 How to Interact

MoxRed operates entirely via Discord chat and slash commands, eliminating the need to expose a Linux bash console or give direct SSH access to staff.

1. **Mentioning the Bot**: Send a direct question or action:
   > `@MoxRed check the server health and tell me the current TPS.`
2. **Slash Commands**: Use quick, built-in commands like `/status` or `/ask`.

---

## 🚀 Core Capabilities

### 1. Automated Player Administration
Ask the bot to manage players interactively without needing to type in-game slash commands.
* *"MoxRed, teleport Steve to Emma's coordinates."*
* *"Please kick JohnDoe for spamming the lobby."*
* *"Give 64 cooked beef to everyone currently online."*

### 2. Multi-Plugin Capabilities Discovery
MoxRed scans and indexes active plugins (such as Vault, Essentials, LuckPerms, ViaVersion, or ProtocolLib) and can check player balances, edit configurations, or query statistics dynamically.
* *"Check how many players are using legacy Minecraft clients via ViaVersion."*
* *"Tell me the player balances managed by Vault."*

### 3. Server Health & Monitoring
Query the real-time status of your Minecraft server safely.
* *"What is the server TPS right now?"*
* *"Is the server experiencing any lag or memory peaks?"*

---

## 🔒 Safety First: Automated Administrative Approval

MoxRed has built-in security constraints designed by SomebodyXPS to ensure that non-admin moderators cannot abuse the AI system:

* **Safe Actions**: Reading statistics, listing players, checking TPS, or getting help do not require manual confirmation and are executed instantly.
* **High-Risk Actions**: Operations such as banishment, config changes, item distribution, or execution of custom server scripts automatically trigger an in-Discord approval prompt. 
* **Approval Workflow**:
  1. The bot formats the requested command.
  2. It posts a rich embed containing **Approve** and **Deny** buttons.
  3. Only users with the designated Administrator/Owner Discord role can press the button to execute the queued action.

---

## 🛠 Building Custom Automations

Because MoxRed is open-source, developers can write custom skill handlers or modify existing behaviors. MoxRed uses a **Deterministic Action Registry** to ensure that all scheduled or queued actions are executed safely within the main server thread, preventing asynchronous thread issues.

Developed by **[SomebodyXPS](https://github.com/SomebodyXPS)**. Contributions are highly welcomed! Please submit issues or pull requests on our GitHub repository.
