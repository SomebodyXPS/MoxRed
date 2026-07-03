# Features & Usage Guide

MoxRed is your AI Server Engineer. Rather than simply running commands, MoxRed understands your entire server environment and can build, configure, automate, maintain, and improve server functionality through conversation.

## How to Interact
You can interact with MoxRed in your Discord server by:
1. **Mentioning the bot:** `@MoxRed create a ticket system for staff reports.`
2. **Using Slash Commands:** `/ask command: "Make a daily rewards system."`

## What Can MoxRed Do?

### 🛠️ Build Server Workflows
Describe what you want to automate, and MoxRed will handle the logic, event listeners, and API connections.
- "Create a Minecraft and Discord account binding system."
- "Create a ticket system for staff reports."
- "Build a daily rewards system."
- "Make a seasonal Halloween event with custom bosses and announcements."

### 🔗 Deep Integrations & Automations
Connect Minecraft, Discord, webhooks, databases, and plugin events together.
- "Automatically create a support channel when someone buys a rank."
- "When a player reaches level 50, unlock the Hero rank and announce it in Discord."
- "Back up the server every 6 hours and notify staff only if the backup fails."

### 🤖 Intelligent Clarification & Safety
MoxRed enforces permissions, validation, and safety before applying changes. If requirements are unclear, it will ask follow-up questions:

**You:**
> *"@MoxRed, create a Minecraft and Discord account binding system. Players should receive a code in-game using /bind, then DM that code to you to link their accounts. After verification, give them the Verified role in Discord and the Member rank in-game."*

**MoxRed:**
> *I can create that. I need a few details:*
> *1. Which Discord role should be assigned?*
> *2. Which permission plugin manages ranks?*
> *3. Should verification codes expire?*
> *4. Should existing linked accounts be allowed to relink?*

After your answers, MoxRed automatically handles:
- Creating the `/bind` command
- Setting up secure verification codes
- Connecting Discord DMs
- Linking Minecraft UUIDs with Discord IDs
- Configuring automatic role and rank assignment
- Enabling the workflow

**No manual coding, no configuration editing.**
