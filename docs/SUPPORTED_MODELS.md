# Supported AI Models

MoxRed gives you the freedom to choose the "brain" behind your server assistant. You can plug in API keys from any of the major AI providers to power MoxRed. It supports any capable AI model of your choice.

## Available Providers

### 1. OpenAI
A widely used AI provider, highly capable at understanding server context and Minecraft commands.
- **Models:** `gpt-4o`, `gpt-4o-mini`
- **Setup:** Use provider `openai` and enter your OpenAI API key in the bot's `config.yml`.

### 2. Google Gemini
Fast and highly capable models from Google.
- **Models:** `gemini-2.5-pro`, `gemini-2.5-flash`
- **Setup:** Use provider `gemini` and enter your Google AI Studio API key in the bot's `config.yml`.

### 3. Anthropic Claude
Models from Anthropic, known for reasoning and conversational abilities.
- **Models:** `claude-3.7-sonnet`, `claude-3.5-sonnet`
- **Setup:** Use provider `claude` and enter your Anthropic API key in the bot's `config.yml`.

### 4. OpenRouter (Access to DeepSeek, Llama, and more)
An excellent gateway to run open-source models or models from DeepSeek, Meta, and Qwen.
- **Models:** `deepseek-chat`, `llama-3.3-70b`, `qwen2.5-72b`
- **Setup:** Use provider `openrouter` and enter your OpenRouter API key in the bot's `config.yml`.

## How to Switch Models
In your `MoxRed-Bot` configuration folder, open `config.yml` and locate the `ai` section:

```yaml
ai:
  provider: "openai" # Change to your preferred provider
  model: "gpt-4o" # Change to your preferred model
  apiKey: "YOUR_API_KEY_HERE"
```
Restart your bot, and MoxRed will instantly switch to the new AI brain!
