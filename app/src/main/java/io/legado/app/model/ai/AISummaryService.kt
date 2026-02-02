package io.legado.app.model.ai

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
}
