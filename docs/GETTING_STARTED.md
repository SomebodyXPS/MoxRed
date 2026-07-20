# Getting Started with MoxRed

Setting up MoxRed is easy! MoxRed is an open-source Minecraft server assistant created by **SomebodyXPS**. It comes in two parts: the **Minecraft Plugin (`MoxRed-Core`)** (which runs inside your server) and the **Discord Bot (`MoxRed-Bot`)** (which manages the AI and Discord interactions).

---

## Prerequisites

* A Minecraft server running **Spigot** or **Paper** (recommended) for version **1.16+** (built and tested on **1.21.11**).
* **Java 17+** (Java 21 or 25 is highly recommended) installed on both your Minecraft server host and the Discord bot host.
* A Discord account with administrative permissions to create and invite a bot.
* An AI provider API key (such as Google Gemini, OpenAI, Claude, or OpenRouter).

---

## Step 1: Install the Minecraft Plugin

1. Download the latest `MoxRed-Core-beta-1.0.0.jar` from the [GitHub Releases](https://github.com/SomebodyXPS/MoxRed/releases) page.
2. Place the `.jar` file into your Minecraft server's `plugins/` folder.
3. Start or restart your Minecraft server to let the plugin load and generate its default configuration.
4. Open the generated file `/plugins/MoxRed-Core/config.yml` on your server.
5. Note down the `server.secret` and the port configured (default is `3000`). You will need these to establish a secure connection with the Discord bot.

---

## Step 2: Setup the Discord Bot

1. Download the latest `MoxRed-Bot-beta-1.0.0.jar` from the [GitHub Releases](https://github.com/SomebodyXPS/MoxRed/releases) page.
2. Create a clean folder on your computer or server host (e.g., `moxred-bot/`) and place the `.jar` there.
3. Run the bot once to generate the default configuration:
   ```bash
   java -jar MoxRed-Bot-beta-1.0.0.jar
   ```
4. Stop the bot. Open the newly generated `config.yml` file and configure the parameters:
   * **Discord Token**: Obtain a Discord Bot Token from the [Discord Developer Portal](https://discord.com/developers/applications).
   * **AI API Key**: Enter your API key (e.g., Google Gemini or OpenAI) under the `ai` block.
   * **Minecraft Connection**: Put in your Minecraft server's IP, Port, and the `secret` obtained in Step 1.
5. Save the configuration and start the bot again.

---

## Step 3: Invite the Bot and Verify

1. Go to your application in the [Discord Developer Portal](https://discord.com/developers/applications).
2. Under the **OAuth2** -> **URL Generator** tab, select the following scopes:
   * `bot`
   * `applications.commands`
3. In bot permissions, ensure the bot has permission to view channels, send messages, and embed links.
4. Copy the generated URL, paste it into your browser, and authorize the bot for your Discord server.
5. Once the bot connects, type `/status` in a Discord channel to confirm that MoxRed-Bot has successfully connected to MoxRed-Core on your Minecraft server.

---

Developed and maintained by **[SomebodyXPS](https://github.com/SomebodyXPS)**. For issues or contributions, please open a pull request on our GitHub repository.
