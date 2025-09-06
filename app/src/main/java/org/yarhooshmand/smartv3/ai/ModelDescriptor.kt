package org.yarhooshmand.smartv3.ai

data class ModelDescriptor(
    val id: String,
    val display: String,
    val provider: ProviderType,
    val notes: String = ""
)
