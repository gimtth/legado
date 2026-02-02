package io.legado.app.data.entities

/**
 * AI 推荐书籍数据模型
 */
data class AIBookRecommendation(
    val title: String,
    val author: String,
    val reason: String,
    val tags: List<String>
)

/**
 * AI 推荐响应
 */
data class AIRecommendationResponse(
    val recommendations: List<AIBookRecommendation>
)

/**
 * 对话消息
 */
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val recommendations: List<AIBookRecommendation>? = null,
    val isLoading: Boolean = false,
    val isWelcome: Boolean = false  // 是否为欢迎消息（不保存到历史）
)
