# Getting Started with MoxRed

Setting up MoxRed is easy! MoxRed consists of two components that work together:

- **MoxRed-Core** (Minecraft Plugin) — runs inside your server
- **MoxRed-Bot** (Discord Bot) — manages AI and Discord interactions

---

## 📋 Prerequisites

Before you begin, ensure you have the following:

- ✅ A Minecraft server running **Spigot** or **Paper** (version **1.21.11** recommended, compatible with 1.16+)
- ✅ **Java 17+** installed (Java 21 or 25 highly recommended) on both the server host and bot host
- ✅ A Discord account with administrative permissions to create and invite a bot
- ✅ An AI provider API key (Google Gemini, OpenAI, Claude, or OpenRouter)

---

## 📘 Step 1: Install the Minecraft Plugin

1. Download the latest `MoxRed-Core-beta-1.0.0.jar` from the [GitHub Releases](https://github.com/SomebodyXPS/MoxRed/releases) page.
2. Place the `.jar` file into your Minecraft server's `plugins/` folder.
3. Start or restart your Minecraft server to let the plugin load and generate its default configuration.
4. Open the generated file `/plugins/MoxRed-Core/config.yml`.
5. Note down the `server.secret` and the configured port (default: `3000`). You will need these to connect the Discord bot.

> 💡 **Tip**: The `server.secret` is used to authenticate the WebSocket connection between the plugin and the bot. Keep it secure!

---

## 📗 Step 2: Setup the Discord Bot

1. Download the latest `MoxRed-Bot-beta-1.0.0.jar` from the [GitHub Releases](https://github.com/SomebodyXPS/MoxRed/releases) page.
2. Create a clean folder on your computer or server host (e.g., `moxred-bot/`) and place the `.jar` there.
3. Run the bot once to generate the default configuration:
   ```bash
   java -jar MoxRed-Bot-beta-1.0.0.jar
   ```
4. Stop the bot. Open the newly generated `config.yml` and configure the following parameters:
   - **Discord Token**: Obtain a bot token from the [Discord Developer Portal](https://discord.com/developers/applications).
   - **AI API Key**: Enter your API key under the `ai` block (see [Supported Models](SUPPORTED_MODELS.md)).
   - **Minecraft Connection**: Add your server's IP, Port, and the `secret` from Step 1.
5. Save the configuration and start the bot again.

### Example `config.yml`

```yaml
discord:
  token: "YOUR_DISCORD_BOT_TOKEN"
  commandPrefix: "!"

ai:
  provider: "gemini"
  model: "gemini-2.5-flash"
  apiKey: "YOUR_API_KEY"

minecraft:
  host: "localhost"
  port: 3000
  secret: "YOUR_SERVER_SECRET"
```

---

## 📙 Step 3: Invite the Bot and Verify

1. Go to your application in the [Discord Developer Portal](https://discord.com/developers/applications).
2. Navigate to the **OAuth2 → URL Generator** section.
3. Select the following scopes: `bot` and `applications.commands`.
4. Select the following permissions: `Send Messages`, `Read Message History`, `Use External Emojis`, `Embed Links`.
5. Copy the generated URL and invite the bot to your server.
6. In Discord, type `@MoxRed ping` to verify the bot is online and connected.

---

## ✅ Verification Checklist

Before using MoxRed, confirm the following:

- [ ] The Minecraft plugin is loaded and the WebSocket server is running
- [ ] The Discord bot is online and responds to `@MoxRed ping`
- [ ] The bot can connect to the Minecraft server via WebSocket
- [ ] The AI provider is configured and the bot responds to natural language queries

---

## 🔧 Troubleshooting

### Bot not responding
- Verify the Discord token is correct and the bot has been invited to the server.
- Check that the bot has the necessary permissions in the Discord channel.

### Plugin not connecting to bot
- Confirm the `server.secret` and `port` in the plugin config match the bot's `minecraft` configuration.
- Ensure both the plugin and bot are running on the same network (or that firewall rules allow WebSocket connections on the configured port).

### AI not responding
- Verify your API key is valid and has not expired.
- Check that your AI provider quota has not been exceeded.
- Ensure the `provider` and `model` fields in `config.yml` are correctly set.

### Server lag or performance issues
- MoxRed runs inside the server thread. If you experience lag, try reducing the frequency of AI queries or upgrading your server resources.

---

## 📚 Next Steps

Now that MoxRed is running, explore more:

- **[Features & Usage Guide →](FEATURES_AND_USAGE.md)** — Learn about all capabilities and custom automations
- **[Supported AI Models →](SUPPORTED_MODELS.md)** — Configure your preferred AI provider
- **[Trademark Policy →](../TRADEMARKS.md)** — Understand usage guidelines

---

<div align="center">

**Made with ❤️ by [SomebodyXPS](https://github.com/SomebodyXPS)**

</div>
