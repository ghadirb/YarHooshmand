package org.yarhooshmand.smartv3.ai

object ModelAllowlist {
    val openAi = listOf(
        ModelDescriptor("gpt-4o", "GPT-4o (OpenAI)", ProviderType.OPENAI, "Multilingual, very strong"),
        ModelDescriptor("gpt-4o-mini", "GPT-4o mini (OpenAI)", ProviderType.OPENAI, "Fast & cheap")
    )
    val openRouter = listOf(
        ModelDescriptor("openai/gpt-4o", "GPT-4o via OpenRouter", ProviderType.OPENROUTER),
        ModelDescriptor("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet", ProviderType.OPENROUTER),
        ModelDescriptor("meta-llama/llama-3.1-70b-instruct", "Llama 3.1 70B Instruct", ProviderType.OPENROUTER),
        ModelDescriptor("qwen/qwen2-72b-instruct", "Qwen2 72B Instruct", ProviderType.OPENROUTER),
        ModelDescriptor("mistralai/mistral-large", "Mistral Large", ProviderType.OPENROUTER)
    )
    val anthropic = listOf(
        ModelDescriptor("claude-3-5-sonnet", "Claude 3.5 Sonnet (Anthropic)", ProviderType.ANTHROPIC),
        ModelDescriptor("claude-3-opus", "Claude 3 Opus (Anthropic)", ProviderType.ANTHROPIC)
    )
    val defaultsByProvider: Map<ProviderType, ModelDescriptor> = mapOf(
        ProviderType.OPENAI to openAi.first(),
        ProviderType.OPENROUTER to openRouter.first(),
        ProviderType.ANTHROPIC to anthropic.first()
    )
}
