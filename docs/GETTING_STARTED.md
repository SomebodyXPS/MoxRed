# Getting Started with MoxRed

Setting up MoxRed is easy! MoxRed comes in two parts: the **Minecraft Plugin** (which runs on your server) and the **Discord Bot** (which powers the AI).

## Step 1: Install the Minecraft Plugin
1. Download the `MoxRed-Core.jar` file from your purchase panel.
2. Place the file into your Minecraft server's `plugins/` folder.
3. Restart your Minecraft server.
4. Open the generated `plugins/MoxRed-Core/config.yml` file and note down the `server.secret`. You will need this to connect the Discord bot.

## Step 2: Setup the Discord Bot
1. Download the `MoxRed-Bot.jar` file.
2. Run the bot on your computer or a hosting service using Java 17+.
3. The bot will create a `config.yml` file. Open it and enter:
   - Your **Discord Bot Token** (from the Discord Developer Portal).
   - Your **AI API Key** (e.g., OpenAI or Gemini).
   - The **Minecraft Server IP, Port, and Secret** (from Step 1).
4. Restart the bot.

## Step 3: Invite the Bot to Discord
1. Generate an invite link from the Discord Developer Portal with the `bot` and `applications.commands` scopes.
2. Invite MoxRed to your Discord server.
3. Type `/status` in your Discord server to confirm the connection!

Need help? Contact our support team!
