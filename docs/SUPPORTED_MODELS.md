# Supported AI Models

MoxRed is designed with high flexibility, allowing you to choose the "brain" behind your server assistant. Developed by **SomebodyXPS**, the bot's core architecture supports standard JSON payloads to interface with several world-class Large Language Models.

---

## 🧠 Supported Providers & Models

You can choose from the following providers by configuring your `config.yml` file:

### 1. Google Gemini (Recommended)
Excellent reasoning, incredibly fast execution times, and unmatched pricing or free tier availability.
* **Suggested Models**: `gemini-2.5-pro`, `gemini-2.5-flash`
* **Configuration Setup**: Set provider to `gemini` and enter your Gemini API key.

### 2. OpenAI
Highly capable models for code and command generation with outstanding system prompt adherence.
* **Suggested Models**: `gpt-4o`, `gpt-4o-mini`
* **Configuration Setup**: Set provider to `openai` and enter your OpenAI API key.

### 3. Anthropic Claude
Industry-leading models for conversational accuracy, complex coding, and reasoning tasks.
* **Suggested Models**: `claude-3.7-sonnet`, `claude-3.5-sonnet`
* **Configuration Setup**: Set provider to `claude` and enter your Anthropic API key.

### 4. OpenRouter
A unified API gateway that allows you to run high-quality open-source models or models from DeepSeek, Meta, and Qwen.
* **Suggested Models**: `deepseek-chat`, `llama-3.3-70b`, `qwen2.5-72b`
* **Configuration Setup**: Set provider to `openrouter` and enter your OpenRouter API key.

---

## ⚙ How to Configure Your AI Brain

In your `MoxRed-Bot` application directory, locate and open your `config.yml` file, then edit the `ai` section:

```yaml
ai:
  provider: "gemini"             # Options: "gemini", "openai", "claude", "openrouter"
  model: "gemini-2.5-flash"      # Put your desired model identifier here
  apiKey: "AI_STUDIO_API_KEY"   # Insert your official API key
```

Save the file and restart the Discord bot. MoxRed will dynamically reload the Advanced AI Service and utilize your chosen model on subsequent user prompts.

---

Designed and developed by **[SomebodyXPS](https://github.com/SomebodyXPS)**.
