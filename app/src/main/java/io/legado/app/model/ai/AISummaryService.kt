package io.legado.app.model.ai

import io.legado.app.data.entities.AIBookRecommendation
import io.legado.app.data.entities.AIRecommendationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * AI 摘要服务
 */
object AISummaryService {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    
    /**
     * 生成章节摘要
     * @param provider AI 服务提供商：deepseek, glm, gemini
     * @param apiKey API 密钥
     * @param bookName 书名
     * @param chapterTitle 章节标题
     * @param content 章节内容
     * @return 摘要文本
     */
    suspend fun generateSummary(
        provider: String,
        apiKey: String,
        bookName: String,
        chapterTitle: String,
        content: String
    ): String = withContext(Dispatchers.IO) {
        
        // 预处理内容：去除多余空白、特殊字符
        val cleanContent = content
            .replace(Regex("\\s+"), " ")
            .replace(Regex("[\\x00-\\x1F\\x7F]"), "")
            .trim()
        
        // 智能截断：保留完整句子，限制在 3000 字以内
        val limitedContent = if (cleanContent.length > 3000) {
            val truncated = cleanContent.substring(0, 3000)
            val lastPeriod = truncated.lastIndexOfAny(charArrayOf('。', '！', '？', '.', '!', '?'))
            if (lastPeriod > 0) {
                truncated.substring(0, lastPeriod + 1)
            } else {
                truncated + "..."
            }
        } else {
            cleanContent
        }
        
        val prompt = """
请为小说《$bookName》的章节"$chapterTitle"生成一段简洁的摘要。

要求：
1. 100-200字
2. 概括本章主要情节
3. 提及关键人物和事件
4. 语言简洁流畅

章节内容：
$limitedContent

请直接输出摘要，不要其他内容。
        """.trimIndent()
        
        when (provider.lowercase()) {
            "deepseek" -> callDeepSeek(apiKey, prompt)
            "glm" -> callGLM(apiKey, prompt)
            "gemini" -> callGemini(apiKey, prompt)
            else -> throw IllegalArgumentException("不支持的 AI 服务: $provider")
        }
    }
    
    /**
     * 调用 DeepSeek API
     */
    private fun callDeepSeek(apiKey: String, prompt: String): String {
        val url = "https://api.deepseek.com/v1/chat/completions"
        
        val json = JSONObject().apply {
            put("model", "deepseek-chat")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 500)
        }
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("DeepSeek API 调用失败: ${response.code} ${response.message}")
            }
            
            val responseBody = response.body?.string() ?: throw Exception("响应为空")
            val jsonResponse = JSONObject(responseBody)
            
            return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }
    
    /**
     * 调用 GLM API (智谱清言)
     */
    private fun callGLM(apiKey: String, prompt: String): String {
        val url = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
        
        val json = JSONObject().apply {
            put("model", "glm-4-flash")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 500)
        }
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("GLM API 调用失败: ${response.code} ${response.message}")
            }
            
            val responseBody = response.body?.string() ?: throw Exception("响应为空")
            val jsonResponse = JSONObject(responseBody)
            
            return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }
    
    /**
     * 调用 Gemini API
     */
    private fun callGemini(apiKey: String, prompt: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Gemini API 调用失败: ${response.code} ${response.message}")
            }
            
            val responseBody = response.body?.string() ?: throw Exception("响应为空")
            val jsonResponse = JSONObject(responseBody)
            
            return jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
                .trim()
        }
    }
    
    /**
     * 推荐书籍
     * @param provider AI 服务提供商
     * @param apiKey API 密钥
     * @param userInput 用户输入的需求描述
     * @return 推荐书籍列表
     */
    suspend fun recommendBooks(
        provider: String,
        apiKey: String,
        userInput: String
    ): AIRecommendationResponse = withContext(Dispatchers.IO) {
        
        val prompt = """
你是一个专业的网络小说推荐助手。用户会告诉你他想看什么类型的书，你需要推荐5本符合要求的网络小说。

要求：
1. 必须返回JSON格式
2. 每本书包含：书名(title)、作者(author)、推荐理由(reason，50字内)、标签(tags，数组)
3. 推荐的书要真实存在且知名度较高
4. 优先推荐完结作品或热门连载

用户需求：$userInput

请严格按照以下JSON格式返回，不要有任何其他内容：
{
  "recommendations": [
    {"title": "书名", "author": "作者", "reason": "推荐理由", "tags": ["标签1", "标签2"]}
  ]
}
        """.trimIndent()
        
        val responseText = when (provider.lowercase()) {
            "deepseek" -> callDeepSeek(apiKey, prompt)
            "glm" -> callGLM(apiKey, prompt)
            "gemini" -> callGemini(apiKey, prompt)
            else -> throw IllegalArgumentException("不支持的 AI 服务: $provider")
        }
        
        // 解析 JSON 响应
        parseRecommendationResponse(responseText)
    }
    
    /**
     * 解析推荐响应
     */
    private fun parseRecommendationResponse(responseText: String): AIRecommendationResponse {
        try {
            // 尝试提取 JSON 部分（有时 AI 会在前后添加说明文字）
            val jsonStart = responseText.indexOf("{")
            val jsonEnd = responseText.lastIndexOf("}") + 1
            
            if (jsonStart == -1 || jsonEnd <= jsonStart) {
                throw Exception("响应中未找到有效的 JSON 格式")
            }
            
            val jsonText = responseText.substring(jsonStart, jsonEnd)
            val jsonObject = JSONObject(jsonText)
            val recommendationsArray = jsonObject.getJSONArray("recommendations")
            
            val recommendations = mutableListOf<AIBookRecommendation>()
            for (i in 0 until recommendationsArray.length()) {
                val item = recommendationsArray.getJSONObject(i)
                
                val tags = mutableListOf<String>()
                val tagsArray = item.optJSONArray("tags")
                if (tagsArray != null) {
                    for (j in 0 until tagsArray.length()) {
                        tags.add(tagsArray.getString(j))
                    }
                }
                
                recommendations.add(
                    AIBookRecommendation(
                        title = item.getString("title"),
                        author = item.getString("author"),
                        reason = item.getString("reason"),
                        tags = tags
                    )
                )
            }
            
            return AIRecommendationResponse(recommendations)
            
        } catch (e: Exception) {
            throw Exception("解析推荐结果失败: ${e.message}\n原始响应: $responseText")
        }
    }
}
