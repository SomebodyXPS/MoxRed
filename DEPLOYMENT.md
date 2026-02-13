# MoxRed Deployment Guide

Complete setup instructions for deploying MoxRed-Core + MoxRed-Bot

---

## Prerequisites

### Server Requirements
- Java 17+ JDK
- Maven 3.9+
- Spigot 1.20.4 or later (with bukkit.yml configured)
- Internet connection (for Maven dependencies)

### Discord Requirements
- Discord bot application (created in Discord Developer Portal)
- Discord bot token
- Admin role ID from your Discord server

### Network Requirements
- Port 9090 accessible between bot and core plugin
- Outbound HTTPS (443) for Discord API
- Firewall rules configured

---

## Part 1: Build Projects

### Step 1.1: Build Core Plugin

```bash
cd /workspaces/MoxRed

# Clean and build
mvn clean package -U

# Output: 
# - moxred-core/target/MoxRed-Core-1.0.0.jar
# - moxred-bot/target/MoxRed-Bot-1.0.0.jar (after bot build)
```

**Expected Output**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 60s
[INFO] Finished at: 2024-XX-XX HXX:XXZ
```

### Step 1.2: Build Bot Plugin

```bash
cd /workspaces/MoxRed/moxred-bot

# Make build script executable
chmod +x build.sh

# Build
./build.sh

# Or manually:
mvn clean package -U
```

**Output**: 
- `moxred-bot/target/MoxRed-Bot-1.0.0.jar`

---

## Part 2: Configure Core Plugin

### Step 2.1: Deploy JAR to Server

```bash
# Copy core plugin JAR to server
cp /workspaces/MoxRed/moxred-core/target/MoxRed-Core-1.0.0.jar \
   /path/to/spigot/server/plugins/

# Or if running locally:
cp /workspaces/MoxRed/moxred-core/target/moxred-core-1.0.0.jar \
   ~/SpigotServer/plugins/
```

### Step 2.2: Start Server (First Run)

```bash
# Navigate to server directory
cd ~/SpigotServer

# Start server
java -Xmx1024M -Xms1024M -jar spigot-1.20.4.jar nogui

# Look for:
# [MoxRed-Core] Initializing MoxRed-Core Plugin...
# [MoxRed-Core] WebSocket Server listening on port 9090
```

### Step 2.3: Configure Core Plugin

Server will auto-create `plugins/MoxRed-Core/config.yml`:

```yaml
# MoxRed-Core Plugin Configuration

# Features
features:
  enableWorkflowMode: true
  enableRawConsole: false
```

### Step 2.4: Generate Strong Shared Secret

~~Removed~~ - No longer needed for local bot communication

### Step 2.5: Restart Server

```bash
# Use server console command (if running):
# > restart

# Or restart via system:
pkill -f spigot-1.20.4.jar
cd ~/SpigotServer
java -Xmx1024M -Xms1024M -jar spigot-1.20.4.jar nogui
```

**Verify**:
```
[MoxRed-Core] Initializing MoxRed-Core Plugin...
[MoxRed-Core] Loading configuration from plugins/MoxRed-Core/config.yml
[MoxRed-Core] Execution engine initialized
[MoxRed-Core] MoxRed-Core Plugin enabled successfully
```

---

## Part 3: Configure Discord Bot

### Step 3.1: Create Discord Bot Application

1. Go to https://discord.com/developers/applications
2. Click "New Application"
3. Name it "MoxRed" (or your choice)
4. Go to "Bot" section
5. Click "Add Bot"
6. Under TOKEN, click "Copy"
7. Save this token securely

### Step 3.2: Set Bot Permissions

In Discord Developer Portal:
1. Go to OAuth2 → URL Generator
2. Scopes: `bot`
3. Permissions: 
   - Send Messages
   - Embed Links
   - Read Message History
   - Slash Commands
   - Use Application Commands
4. Copy generated URL
5. Open URL in browser to invite bot to your server

### Step 3.3: Get Admin Role ID

1. In Discord server settings, enable Developer Mode (Settings → Advanced → Developer Mode)
2. Right-click your admin role
3. Click "Copy User ID" (or Role ID)
4. Save this ID

### Step 3.4: Configure Bot

Edit `/workspaces/MoxRed/moxred-bot/config.yml`:

```yaml
# MoxRed-Bot Configuration

# Discord Bot Token (from step 3.1)
botToken: "YOUR_DISCORD_BOT_TOKEN_HERE"

# Core Plugin Server Configuration
# Admin Role ID (from step 3.3)
adminRoleId: "YOUR_ADMIN_ROLE_ID_HERE"

# AI Integration (Gemini 2.5 Flash)
ai:
  enabled: true
  geminiApiKey: "YOUR_GEMINI_API_KEY"

# Enable verbose logging
verboseLogging: false
```

**Critical Verifications**:
```
✓ botToken: Not empty, starts with MQ...
✓ adminRoleId: Numeric Discord ID (18+ digits)
✓ ai.enabled: true
✓ ai.geminiApiKey: Valid Gemini API key
```

---

## Part 4: Run MoxRed Bot

### Step 4.1: Start Bot

```bash
cd /workspaces/MoxRed/moxred-bot

# Run the bot
java -jar target/MoxRed-Bot-1.0.0.jar
```

**Expected Output**:
```
[MoxRedBot] Starting MoxRed-Bot v1.0.0
[MoxRedBot] Initializing Discord bot...
[MoxRedBot] Discord bot ready as: MoxRed#1234
[MoxRedBot] Listening for natural language messages...
```

### Step 4.2: Verify Connection

Check bot status:
- Bot should be online in Discord server
- No error messages about plugin connection

Check core plugin logs:
- Should show MoxRed-Core plugin loaded
- Check `plugins/MoxRed-Core/audit.log` for activity

---

## Part 5: Test Commands

### Test 1: Broadcast Message

```
"Broadcast MoxRed is working!"
```

**Expected**:
- All players see: `[Server] MoxRed is working!`
- Bot responds: "✅ Broadcast Sent"
- Core plugin logs action

### Test 2: Natural Language

```
"Give Steve a diamond sword"
```

**Expected**:
- Bot understands the request
- Executes action via MoxRed-Core
- Responds with confirmation

---

## Part 6: Troubleshooting

### Bot Won't Connect to Core Plugin

**This is now a local connection - no network issues**

**Checks**:
1. Is core plugin running?
   ```bash
   # Check plugin.yml shows "MoxRed-Core"
   head -5 plugins/MoxRed-Core/plugin.yml
   ```

2. Check bot logs for errors about loading core plugin

### Commands Not Working (Timeout)

**Error**: `Failed to execute action`

**Checks**:
1. Bot has admin role assigned?
2. Core plugin is running?
3. Check bot logs for detailed error messages

### Packet Signature Errors

~~Removed~~ - No longer applicable for local communication

### Bot Token Invalid

**Error**: `Invalid Discord bot token`

**Fix**:
1. Regenerate token in Discord Developer Portal
2. Copy new token (no spaces)
3. Update config.yml
4. Restart bot

### Admin Role Not Found

**Error**: Users can't access commands (permission denied)

**Fix**:
1. Verify admin role ID is correct
2. Verify user is assigned to that role
3. Wait 1 minute for Discord cache to update

---

## Part 7: Monitoring

### Check Core Plugin Health

View server console:
```
[MoxRed] Action executed: BROADCAST
[MoxRed] Telemetry: packets_received=10, actions_executed=9
```

Check audit log:
```bash
cat plugins/MoxRed/audit.log

# Shows each action with timestamp and result:
[2024-01-15 12:34:56] BROADCAST - SUCCESS - {"message":"Hello"}
[2024-01-15 12:35:10] OP_PLAYER - SUCCESS - {"playerName":"Steve"}
```

### Check Bot Logs

Enable verbose logging in `config.yml`:
```yaml
verboseLogging: true
```

Watch bot output:
```
[CorePluginClient] Connected to core plugin
[CorePluginClient] Sending: {...}
[CorePluginClient] Received: {...}
[DiscordEventHandler] Broadcast successful
```

---

## Part 8: Production Deployment

### Security Hardening

1. **Network Security**
   ```bash
   # Only allow bot IP to access core plugin
   sudo ufw allow from BOT_IP to any port 9090
   ```

2. **Firewall Rules**
   - Core server port 9090: Only from bot server
   - Discord bot port: Outbound only
   - SSH port: Limited IPs only

3. **Shared Secret Management**
   ```bash
   # Generate strong secret
   openssl rand -hex 40
   
   # Store securely (not in git)
   # Rotate quarterly
   # Use environment variables in production
   ```

4. **Discord Bot Token**
   ```bash
   # Never commit to git!
   # Use environment variables:
   export MOXRED_BOT_TOKEN="your-token"
   # Then in code: System.getenv("MOXRED_BOT_TOKEN")
   ```

### Systemd Service (Linux)

Create `/etc/systemd/system/moxred-bot.service`:
```ini
[Unit]
Description=MoxRed Discord Bot
After=network.target

[Service]
Type=simple
User=moxred
WorkingDirectory=/opt/moxred-bot
ExecStart=/usr/bin/java -jar target/moxred-bot-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Start service:
```bash
sudo systemctl enable moxred-bot
sudo systemctl start moxred-bot
sudo systemctl status moxred-bot
```

### Monitoring

Set up monitoring for:
- Core plugin WebSocket connection health
- Bot Discord connection status
- Packet latency
- Error rates

```bash
# Check bot is running
ps aux | grep moxred-bot

# Check core plugin logs
tail -f ~/SpigotServer/plugins/MoxRed/audit.log

# Monitor resource usage
watch 'ps aux | grep java'
```

---

## Part 9: Upgrading

### Backup Before Upgrade

```bash
# Backup core plugin
cp plugins/MoxRed/config.yml plugins/MoxRed/config.yml.backup

# Backup bot config
cp moxred-bot/config.yml moxred-bot/config.yml.backup
```

### Update Core Plugin

```bash
cd /workspaces/MoxRed
git pull origin main
mvn clean package -U

# Stop server
# Copy new JAR
cp target/moxred-core-1.0.0.jar ~/SpigotServer/plugins/
# Start server
```

### Update Bot

```bash
cd /workspaces/MoxRed/moxred-bot
git pull origin main
./build.sh

# Stop bot (Ctrl+C)
# Start with new JAR
java -jar target/moxred-bot-1.0.0.jar
```

---

## Support

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| Bot won't start | Invalid token | Regenerate in Discord Developer Portal |
| Commands timeout | Network delay | Check firewall/network connectivity |
| Signature errors | Secret mismatch | Verify exact match in both configs |
| Bot offline | Connection lost | Check core plugin is running |
| Commands locked | Wrong role | Verify role ID and assignment |

### Logs to Check

1. **Core Plugin**: `plugins/MoxRed-Core/audit.log`
2. **Bot Console**: [MoxRedBot], [AdvancedAIService], [DiscordEventHandler]
3. **Discord**: Bot online status, permissions
4. **Server Console**: Plugin load messages

---

## Rollback Procedure

If issues occur:

```bash
# Stop bot
# Stop server
# Replace JAR with backup
cp ~/backup/MoxRed-Core-1.0.0.jar ~/SpigotServer/plugins/

# Restore config
cp plugins/MoxRed-Core/config.yml.backup plugins/MoxRed-Core/config.yml

# Start server and bot
```

---

**Status**: Ready for Production ✅
