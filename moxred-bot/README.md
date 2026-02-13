# MoxRed-Bot

**AI-Powered Discord Control Layer for MoxRed**

The MoxRed-Bot is a Discord bot that connects to the MoxRed-Core Plugin and exposes its functionality through natural language conversation. It handles natural language understanding and communicates directly with the core plugin using local method calls.

## Architecture

```
Discord Server (Users)
    ↓
MoxRed-Bot Plugin (Discord JDA + Gemini AI)
    ↓
MoxRed-Core Plugin (Local Spigot/PaperMC)
    ↓
Minecraft Server (Actions + Live Context)
```

## Features

- **Natural Language Interface**: Tell the bot what to do in plain English
- **Live Server Context**: AI has real-time access to player, world, and server data
- **Conversation History**: Multi-turn conversations with memory within 1-hour sessions
- **AI-Powered Actions**: Gemini 2.5 Flash understands intent and recommends safe actions
- **Role-Based Authorization**: Admin role restriction
- **Direct Plugin Communication**: No WebSocket overhead—local method calls
- **Safe Action Execution**: Whitelisted action set (BROADCAST, OP, DEOP, GIVE_ITEM, GIVE_RANK, GET_TPS)
- **Confidence Scoring**: AI explains what it plans to do before execution
- **Audit Trail**: All actions logged to MoxRed-Core audit.log

## Components

### Configuration (`BotConfig`)
- Loads config.yml with Discord token, admin role ID, and Gemini API key
- Validates critical configuration on startup
- Provides type-safe configuration access

### Server Context (`ServerContextProvider`)
- Fetches live player data (names, health, location, inventory, OP status, XP)
- Retrieves world information (environment, difficulty, weather, time)
- Collects server metrics (TPS, uptime, version, max players)
- Provides formatted context for AI processing

### Conversation Management (`ConversationManager`)
- Maintains per-user conversation history (50 messages max)
- Auto-clears conversations after 1-hour inactivity
- Provides formatted history for AI context

### AI Engine (`AdvancedAIService`)
- Integrates with Google Gemini 2.5 Flash API
- Processes natural language commands with live server context
- Returns action recommendations with confidence scores
- Handles multi-turn conversations naturally

### Action Execution (`ActionOrchestrator`)
- Validates and executes AI-recommended actions
- Prevents unsafe or non-whitelisted actions
- Executes directly against MoxRed-Core Plugin
- Returns results to Discord users

### Discord Integration (`DiscordEventHandler`)
- Listens for natural language messages from admin users
- Routes messages through AI engine and action orchestrator
- Provides `/help`, `/context`, and `/clear` slash commands
- Shows action explanations and confidence scores
- Formats responses with action results and recommendations

### Main Bot (`MoxRedBotPlugin`)
- Orchestrates bot initialization
- Manages JDA lifecycle and plugin registration
- Coordinates ServerContextProvider, ConversationManager, and AI services

## Setup

### 1. Prerequisites

- Java 17+ JDK
- Maven 3.9+
- Discord bot token from Discord Developer Portal
- Admin role ID from your Discord server
- MoxRed-Core Plugin running on the same Spigot/PaperMC server
- Google Gemini API key

### 2. Configuration

1. Edit `config.yml`:
   ```yaml
   discord:
     token: "YOUR_DISCORD_BOT_TOKEN"
     adminRoleId: "YOUR_ADMIN_ROLE_ID"
   
   ai:
     geminiApiKey: "YOUR_GEMINI_API_KEY"
     model: "gemini-2.5-flash"
   
   features:
     verboseLogging: false
   ```

2. **CRITICAL**: MoxRed-Core Plugin must be running before MoxRed-Bot starts

3. Both plugins run on the same Spigot/PaperMC server (local plugin-to-plugin communication)

### 3. Build

```bash
mvn clean package
```

Output JAR: `target/MoxRed-Bot.jar`

### 4. Deploy

1. Copy `target/MoxRed-Bot.jar` to `plugins/` directory
2. Restart Spigot/PaperMC server
3. Bot will initialize on server startup

The bot will:
1. Load configuration from `config.yml`
2. Connect to the local MoxRed-Core plugin
3. Connect to Discord
4. Start listening for mentions from admin users only
5. Initialize ServerContextProvider and ConversationManager

## Usage

The MoxRed-Bot ONLY responds to mentions from users with the admin role. Mention the bot in Discord and tell it what to do using natural language.

### Examples

**All commands require mentioning the bot:**

**Get server status:**
```
@MoxRed-Bot what's the server status?
```
*AI Response: "Server is running PaperMC 1.20.1 with 5/20 players online. Current TPS is 19.8."*

**Give a player an item:**
```
@MoxRed-Bot give Steve 64 diamonds
```
*AI Response: "I'll give Steve 64 diamonds. Recommended action: GIVE_ITEM with parameters (player: Steve, item: DIAMOND, amount: 64). Confidence: 0.95"*
*After confirmation:* "✅ Gave Steve 64 diamonds"

**Broadcast a message:**
```
@MoxRed-Bot tell everyone the server is restarting in 5 minutes
```
*AI Response: "I'll broadcast a message. Recommended action: BROADCAST (message: 'Server is restarting in 5 minutes'). Confidence: 0.98"*

**Promote a player:**
```
@MoxRed-Bot op the player named Alex
```
*AI Response: "I'll grant operator status to Alex. Recommended action: OP_PLAYER (player: Alex). Confidence: 0.99"*

### Slash Commands

While the bot primarily responds to natural language messages, it also provides utility commands:

**`/help`** - Display available actions and examples
**`/context`** - Show current server context the AI can see
**`/clear`** - Clear your conversation history

## Multi-Turn Conversations

The bot maintains conversation history for 1 hour per user. This enables natural multi-turn dialog:

```
User: "What's the current TPS?"
Bot: "Current TPS is 18.5"

User: "That's low. Can you restart the server?"
Bot: "I can't restart the server—it's not in the allowed action list. But I can broadcast a warning to players."
```

## Safe Action Whitelist

MoxRed-Bot can only execute these actions (for safety):

| Action | Parameters | Purpose |
|--------|-----------|---------|
| `BROADCAST` | message (string) | Send message to all players |
| `OP_PLAYER` | player (string) | Grant operator status |
| `DEOP_PLAYER` | player (string) | Revoke operator status |
| `GIVE_ITEM` | player, item, amount | Give items to a player |
| `GIVE_RANK` | player, rank | Assign a permission rank |
| `GET_TPS` | (none) | Retrieve server TPS |

All actions are logged to `plugins/MoxRed-Core/audit.log`.

## Error Handling

If the AI cannot understand a request or an action fails, it will explain why in Discord:

```
User: "Restart the server"
Bot: "I can't restart the server—it's not in the allowed action whitelist. The safe actions I can perform are: BROADCAST, OP_PLAYER, DEOP_PLAYER, GIVE_ITEM, GIVE_RANK, and GET_TPS."
```

## Logging

The bot logs all activity with configurable verbosity:

Enable verbose logging in `config.yml`:
```yaml
features:
  verboseLogging: true
```

Check bot output (console) for:
- Message processing logs
- AI response details
- Action execution results
- Errors and warnings

## Troubleshooting

### Bot won't connect to Discord
- Verify Discord bot token is correct in `config.yml`
- Check bot has read message history and send messages permissions
- Verify bot is in the target server

### Bot won't execute actions
- Check admin user has the configured admin role ID
- Verify MoxRed-Core plugin is running on the server
- Check `plugins/MoxRed-Core/audit.log` for action errors

### Gemini API errors
- Verify API key is correct and has quota remaining
- Check internet connectivity
- Review Gemini API rate limits

### Bot responds incorrectly
- Check conversation history: Use `/context` to see what data the AI can see
- Verify server context is being fetched correctly
- Try clearing conversation with `/clear` and retry

## Development

### Project Structure

```
moxred-bot/
├── pom.xml                                    # Maven configuration
├── src/main/java/com/moxred/bot/
│   ├── MoxRedBotPlugin.java                  # Main bot plugin class
│   ├── ai/
│   │   ├── AdvancedAIService.java            # Gemini API integration
│   │   ├── NaturalLanguageParser.java        # Response parsing
│   │   └── ServerContextProvider.java        # Live server data
│   ├── config/
│   │   └── BotConfig.java                    # Configuration management
│   ├── discord/
│   │   └── DiscordEventHandler.java          # Discord event handling
│   └── execution/
│       ├── ConversationManager.java          # Multi-turn conversation history
│       └── ActionOrchestrator.java           # Action validation and execution
├── src/main/resources/
│   ├── config.yml                            # Default configuration
│   └── plugin.yml                            # Spigot plugin manifest
└── README.md                                 # This file
```

### Dependencies

- **JDA 5.0.0-beta.20**: Discord bot framework
- **Google Generative AI (Gemini REST API)**: LLM integration
- **Jackson 2.15.2**: JSON serialization
- **SnakeYAML 2.0**: YAML configuration

### Extending with Custom Actions

To add a new safe action:

1. Add action to MoxRed-Core `ActionRegistry.java` with a new action name
2. The AI will automatically learn the new action from server context prompts
3. Add unit tests to validate action safety
4. Document in the safe action whitelist

## AI Context

The bot sends live server data to Gemini so it understands:
- Player names, health, location, inventory, OP status, XP level
- World environment, difficulty, weather, time, entities, chunks
- Server TPS, uptime, version, max players
- JVM memory and thread information
- Historical conversation context (50 messages per user, 1-hour timeout)

This context lets the AI give accurate, real-time responses and understand nuanced requests like "give the player with the lowest health an apple."

## Best Practices

1. **Admin Role**: Ensure only trusted users have the admin role
2. **Conversation History**: Cleared automatically after 1 hour, or manually with `/clear`
3. **API Key**: Never commit the Gemini API key to version control
4. **Monitoring**: Check audit logs in `plugins/MoxRed-Core/audit.log` periodically
5. **Context Awareness**: Use `/context` to understand what data the AI can see

## Limitations

- Actions limited to whitelisted set (for safety)
- Conversation history limited to 50 messages per user
- Requires MoxRed-Core plugin on same server
- AI responses depend on Gemini API availability
- No persistence between server restarts (conversation history cleared)

## Version

No version specified - development build

## License

Part of MoxRed project
