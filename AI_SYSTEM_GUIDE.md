# 🤖 MoxRed-Bot AI System Guide

## Overview

MoxRed has been transformed into a **ChatGPT-like AI system** where you can manage your Minecraft server through natural language conversation with live server context.

Instead of remembering slash commands, just **chat naturally** with MoxRed in Discord!

---

## 🎯 How It Works

### 1. **Natural Language Processing**
Send messages in Discord and MoxRed understands what you want:

```
You: "Broadcast the server is updating"
MoxRed: 📢 Broadcasting to all players...

You: "Give Steve a diamond sword"
MoxRed: 📦 Giving 1x diamond_sword to Steve...

You: "How many players are online?"
MoxRed: 👥 Currently 3/20 players online...
```

### 2. **Live Server Context**
The AI always has access to real-time server data:
- 👥 Online player names and status
- 🌍 Active worlds and their properties
- 📊 Server performance (TPS, memory, uptime)
- ⚙️ Server configuration

This means the AI can understand requests like:
- "Give admin to whoever is online"
- "Check if Steve is on the server"
- "What's happening in the Nether?"

### 3. **Conversation History**
MoxRed remembers your previous messages, enabling multi-turn conversations:

```
You: "What's the server status?"
MoxRed: 📊 TPS: 20.0, Players: 5/20, Uptime: 2d 3h

You: "Has it been running long?"
MoxRed: Yes, the server has been up for 2 days and 3 hours...
```

### 4. **AI Suggestions**
When you request an action, MoxRed shows:
- ✅ What it understands you're asking
- 🎯 The action it will execute
- 💯 Confidence level
- ⚡ Live execution result

---

## 📝 Example Conversations

### Example 1: Server Announcement
```
User: "Tell everyone the server is updating in 5 minutes"
MoxRed: 
📢 Broadcasting: Broadcast the server is updating in 5 minutes
Confidence: 95%
✅ Message broadcasted to all players
```

### Example 2: Player Management
```
User: "Make Mike an admin"
MoxRed:
🎖️ Assigning rank admin to Mike
Confidence: 98%
✅ Rank admin assigned to Mike
```

### Example 3: Item Distribution
```
User: "Give each online player 10 diamonds"
MoxRed:
I can give items to individual players. Which player should I give diamonds to?
(Shows list of online players)

User: "Give Steve 10 diamonds"
MoxRed:
📦 Giving 10x diamond to Steve
Confidence: 97%
✅ Given 10x diamond to Steve
```

### Example 4: Contextual Questions
```
User: "Who's online?"
MoxRed:
Currently online:
- Steve (Level 25, Health 20/20)
- Alex (Level 18, Health 15/20)
- Mike (Level 30, Health 20/20)

User: "Is Steve on?"
MoxRed:
Yes, Steve is currently online in the Overworld (Health 20/20)
```

---

## 🎮 Supported Actions

MoxRed can execute these server actions based on natural language:

### 1. **BROADCAST** 📢
Announce messages to all players
```
"Broadcast hello everyone"
"Tell all players maintenance starts soon"
"Announce the server will restart at 3 PM"
```

### 2. **OP_PLAYER** 👑
Grant operator status
```
"Op Steve"
"Give admin to Alex"
"Make Mike operator"
```

### 3. **DEOP_PLAYER** ❌
Remove operator status
```
"Deop Steve"
"Remove admin from Alex"
"Revoke operator from Mike"
```

### 4. **GIVE_ITEM** 📦
Give items with optional enchantments
```
"Give Steve a diamond sword"
"Give Alex 10 diamonds"
"Give Mike a diamond sword with sharpness 5"
"Give Steve 64 diamonds with enchantment id:mending"
```

### 5. **GIVE_RANK** 🎖️
Assign player ranks
```
"Make Steve a builder"
"Give Alex the vip rank"
"Rank Mike as moderator"
```

### 6. **GET_TPS** 📊
Check server status
```
"How's the server?"
"Check TPS"
"What's our performance?"
"Server status?"
```

---

## 🛠️ Setup & Configuration

### Prerequisites
- Java 17+
- Discord bot with token
- Gemini API key (free tier available)
- MoxRed Core plugin running

### Configuration (`config.yml`)

```yaml
# Discord Bot Token
botToken: "YOUR_DISCORD_BOT_TOKEN"

# Admin Role ID (who can control the server)
adminRoleId: "YOUR_ADMIN_ROLE_ID"

# AI Integration
ai:
  enabled: true
  geminiApiKey: "YOUR_GEMINI_API_KEY"

# Logging
verboseLogging: false
```

### Discord Setup

1. **Get Admin Role ID:**
   - Enable Developer Mode in Discord
   - Right-click your admin role
   - Copy Role ID

2. **Create Discord Bot:**
   - Go to Discord Developer Portal
   - Create Application → Add Bot
   - Copy token to `config.yml`
   - Grant these permissions: `Send Messages`, `Embed Links`, `Message History`

3. **Get Gemini API Key:**
   - Visit [Google AI Studio](https://aistudio.google.com/apikey)
   - Create API key
   - Enable Gemini 2.5 Flash model
   - Copy to `config.yml`

### Build & Run

```bash
cd /workspaces/MoxRed/moxred-bot
mvn clean package
java -jar target/moxred-bot-1.0.0.jar
```

---

## 💬 Slash Commands

For advanced features, MoxRed also supports:

- `/help` - Show help information
- `/context` - View current server state
- `/clear` - Clear your conversation history

---

## 🧠 AI System Components

### **ServerContextProvider**
Gathers live server data:
- Player information (names, health, location, inventory)
- World state (biomes, difficulty, weather)
- Server stats (TPS, uptime, memory usage)

### **ConversationManager**
Maintains conversation history and context per user:
- Multi-turn conversations
- Automatic timeout (1 hour of inactivity)
- Up to 50 messages history per user
- `/clear` to reset

### **AdvancedAIService**
Powers the AI understanding:
- Gemini 2.5 Flash integration
- System prompts with live context
- Structured JSON responses
- Confidence scoring

### **ActionOrchestrator**
Executes recommended actions:
- Safety validation
- Parameter normalization
- Action explanation
- Result reporting

---

## 📊 Server State Access

The AI always knows:

```
PLAYERS:
- Online count, max slots
- Each player: name, level, health, location, inventory
- OP status

WORLDS:
- List of loaded worlds
- Environment (Overworld, Nether, End)
- Difficulty, weather, time of day
- Entity count, loaded chunks

SERVER:
- TPS (ticks per second)
- Uptime
- Memory usage
- Loaded plugins
- Version information

PERFORMANCE:
- Heap memory (used/max)
- Active threads
- CPU cores
```

---

## 🔒 Security

- ✅ Only users with admin role can interact
- ✅ All actions are whitelisted
- ✅ Conversation history is per-user
- ✅ Server data is live, never cached
- ✅ Signatures validate all requests
- ✅ Audit logging for all actions

---

## ⚙️ Architecture

```
Discord Message
    ↓
DiscordEventHandler
    ↓
AdvancedAIService (with Gemini)
    ↓ (gets context)
ServerContextProvider
    ↓ (gets history)
ConversationManager
    ↓ (executes)
ActionOrchestrator
    ↓
MoxRed Core Plugin
    ↓
Minecraft Server
```

---

## 🎨 Features

| Feature | Status |
|---------|--------|
| Natural language commands | ✅ |
| Conversation history | ✅ |
| Live server data | ✅ |
| AI suggestions | ✅ |  
| Multi-turn conversation | ✅ |
| Action confidence scoring | ✅ |
| Server context awareness | ✅ |
| Automatic response formatting | ✅ |
| Conversation timeout | ✅ |
| Role-based access control | ✅ |

---

## 🐛 Troubleshooting

### "Not authorized to use commands"
- Make sure user has the admin role configured in `config.yml`
- User must be assigned that role in Discord

### "Gemini API error"
- Check API key is correct
- Verify Gemini 2.5 Flash is enabled in your API console
- Check API key has access to models

### "Core plugin not found"
- Make sure MoxRed Core plugin is loaded first
- Check bot plugin depends on Core plugin in `plugin.yml`

### "Player not found"
- Verify player name spelling (case-sensitive)
- Check player is actually online

---

## 📈 Future Enhancements

Planned features:
- 🎮 Multi-server orchestration
- 📈 Long-term conversation memory
- 🔄 Scheduled tasks ("remind me in 5 minutes")
- 🌐 Web dashboard for monitoring
- 🎯 Custom action definitions
- 🤖 Model selection (GPT-4, Claude, etc.)

---

## 📞 Support

For issues, check:
1. `config.yml` is properly configured
2. Discord bot has correct permissions
3. Gemini API key is active
4. Core plugin logs show no errors
5. Enable `verboseLogging: true` for more details

---

**MoxRed AI: Control your Minecraft server with conversation!** 🚀
