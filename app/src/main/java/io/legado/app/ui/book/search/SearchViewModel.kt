package io.legado.app.ui.book.search

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.legado.app.base.BaseViewModel
import io.legado.app.constant.AppLog
import io.legado.app.data.appDb
import io.legado.app.data.entities.SearchBook
import io.legado.app.data.entities.SearchKeyword
import io.legado.app.help.book.isNotShelf
import io.legado.app.help.config.AppConfig
import io.legado.app.model.webBook.SearchModel
import io.legado.app.utils.ConflateLiveData
import io.legado.app.utils.getPrefString
import io.legado.app.utils.putPrefString
import io.legado.app.utils.toastOnUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(application: Application) : BaseViewModel(application) {
    val handler = Handler(Looper.getMainLooper())
    val bookshelf: MutableSet<String> = ConcurrentHashMap.newKeySet()
    val upAdapterLiveData = MutableLiveData<String>()
    var searchBookLiveData = ConflateLiveData<List<SearchBook>>(1000)
    val searchScope: SearchScope = SearchScope(AppConfig.searchScope)
    var searchFinishLiveData = MutableLiveData<Boolean>()
    var isSearchLiveData = MutableLiveData<Boolean>()
    var searchKey: String = ""
    var hasMore = true
    private var searchID = 0L
    private val searchModel = SearchModel(viewModelScope, object : SearchModel.CallBack {

        override fun getSearchScope(): SearchScope {
            return searchScope
        }

        override fun onSearchStart() {
            isSearchLiveData.postValue(true)
        }

        override fun onSearchSuccess(searchBooks: List<SearchBook>) {
            searchBookLiveData.postValue(searchBooks)
        }

        override fun onSearchFinish(isEmpty: Boolean, hasMore: Boolean) {
            this@SearchViewModel.hasMore = hasMore
            isSearchLiveData.postValue(false)
            searchFinishLiveData.postValue(isEmpty)
        }

        override fun onSearchCancel(exception: Throwable?) {
            isSearchLiveData.postValue(false)
            exception?.let {
                context.toastOnUi(it.localizedMessage)
            }
        }

    })

    init {
        execute {
            appDb.bookDao.flowAll().mapLatest { books ->
                val keys = arrayListOf<String>()
                books.filterNot { it.isNotShelf }
                    .forEach {
                        keys.add("${it.name}-${it.author}")
                        keys.add(it.name)
                        keys.add(it.bookUrl)
                    }
                keys
            }.catch {
                AppLog.put("搜索界面获取书籍列表失败\n${it.localizedMessage}", it)
            }.collect {
                bookshelf.clear()
                bookshelf.addAll(it)
                upAdapterLiveData.postValue("isInBookshelf")
            }
        }.onError {
            AppLog.put("加载书架数据失败", it)
        }
    }

    fun isInBookShelf(book: SearchBook): Boolean {
        val name = book.name
        val author = book.author
        val bookUrl = book.bookUrl
        val key = if (author.isNotBlank()) "$name-$author" else name
        return bookshelf.contains(key) || bookshelf.contains(bookUrl)
    }

    /**
     * 开始搜索
     */
    fun search(key: String) {
        execute {
            if ((searchKey == key) || key.isNotEmpty()) {
                searchModel.cancelSearch()
                searchID = System.currentTimeMillis()
                searchBookLiveData.postValue(emptyList())
                searchKey = key
                hasMore = true
            }
            if (searchKey.isEmpty()) {
                return@execute
            }
            searchModel.search(searchID, searchKey)
        }
    }

    /**
     * 停止搜索
     */
    fun stop() {
        searchModel.cancelSearch()
    }

    fun pause() {
        searchModel.pause()
    }

    fun resume() {
        searchModel.resume()
    }

    /**
     * 保存搜索关键字
     */
    fun saveSearchKey(key: String) {
        execute {
            appDb.searchKeywordDao.get(key)?.let {
                it.usage += 1
                it.lastUseTime = System.currentTimeMillis()
                appDb.searchKeywordDao.update(it)
            } ?: appDb.searchKeywordDao.insert(SearchKeyword(key, 1))
        }
    }

    /**
     * 清楚搜索关键字
     */
    fun clearHistory() {
        execute {
            appDb.searchKeywordDao.deleteAll()
        }
    }

    fun deleteHistory(searchKeyword: SearchKeyword) {
        execute {
            appDb.searchKeywordDao.delete(searchKeyword)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchModel.close()
    }

    // ==================== AI 搜书功能 ====================
    
    val chatMessagesLiveData = MutableLiveData<List<io.legado.app.data.entities.ChatMessage>>()
    val aiSearchErrorLiveData = MutableLiveData<String>()
    
    private val chatMessages = mutableListOf<io.legado.app.data.entities.ChatMessage>()
    private val chatHistoryKey = "ai_book_search_chat_history"
    
    /**
     * 加载聊天历史
     */
    fun loadChatHistory() {
        execute {
            val historyJson = context.getPrefString(chatHistoryKey)
            if (!historyJson.isNullOrEmpty()) {
                try {
                    val jsonArray = JSONArray(historyJson)
                    chatMessages.clear()
                    
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val isUser = item.getBoolean("isUser")
                        val content = item.getString("content")
                        
                        if (isUser) {
                            chatMessages.add(
                                io.legado.app.data.entities.ChatMessage(
                                    content = content,
                                    isUser = true
                                )
                            )
                        } else {
                            // 解析推荐书籍
                            val recommendations = mutableListOf<io.legado.app.data.entities.AIBookRecommendation>()
                            val recsArray = item.optJSONArray("recommendations")
                            if (recsArray != null) {
                                for (j in 0 until recsArray.length()) {
                                    val rec = recsArray.getJSONObject(j)
                                    val tags = mutableListOf<String>()
                                    val tagsArray = rec.optJSONArray("tags")
                                    if (tagsArray != null) {
                                        for (k in 0 until tagsArray.length()) {
                                            tags.add(tagsArray.getString(k))
                                        }
                                    }
                                    recommendations.add(
                                        io.legado.app.data.entities.AIBookRecommendation(
                                            title = rec.getString("title"),
                                            author = rec.getString("author"),
                                            reason = rec.getString("reason"),
                                            tags = tags
                                        )
                                    )
                                }
                            }
                            
                            chatMessages.add(
                                io.legado.app.data.entities.ChatMessage(
                                    content = content,
                                    isUser = false,
                                    recommendations = recommendations.ifEmpty { null }
                                )
                            )
                        }
                    }
                    
                    chatMessagesLiveData.postValue(chatMessages.toList())
                } catch (e: Exception) {
                    AppLog.put("加载聊天历史失败", e)
                }
            }
        }
    }
    
    /**
     * 保存聊天历史
     */
    private fun saveChatHistory() {
        execute {
            try {
                val jsonArray = JSONArray()
                
                chatMessages.forEach { message ->
                    // 不保存加载状态和欢迎消息
                    if (!message.isLoading && !message.isWelcome) {
                        val item = JSONObject()
                        item.put("isUser", message.isUser)
                        item.put("content", message.content)
                        
                        if (!message.recommendations.isNullOrEmpty()) {
                            val recsArray = JSONArray()
                            message.recommendations.forEach { rec ->
                                val recObj = JSONObject()
                                recObj.put("title", rec.title)
                                recObj.put("author", rec.author)
                                recObj.put("reason", rec.reason)
                                
                                val tagsArray = JSONArray()
                                rec.tags.forEach { tag ->
                                    tagsArray.put(tag)
                                }
                                recObj.put("tags", tagsArray)
                                
                                recsArray.put(recObj)
                            }
                            item.put("recommendations", recsArray)
                        }
                        
                        jsonArray.put(item)
                    }
                }
                
                context.putPrefString(chatHistoryKey, jsonArray.toString())
            } catch (e: Exception) {
                AppLog.put("保存聊天历史失败", e)
            }
        }
    }
    
    /**
     * 添加用户消息
     */
    fun addUserMessage(content: String) {
        chatMessages.add(
            io.legado.app.data.entities.ChatMessage(
                content = content,
                isUser = true
            )
        )
        chatMessagesLiveData.postValue(chatMessages.toList())
        saveChatHistory()  // 保存历史
    }
    
    /**
     * 添加 AI 消息
     */
    fun addAIMessage(content: String, recommendations: List<io.legado.app.data.entities.AIBookRecommendation>? = null) {
        chatMessages.add(
            io.legado.app.data.entities.ChatMessage(
                content = content,
                isUser = false,
                recommendations = recommendations
            )
        )
        chatMessagesLiveData.postValue(chatMessages.toList())
        saveChatHistory()  // 保存历史
    }
    
    /**
     * 添加欢迎消息（不保存到历史）
     */
    fun addWelcomeMessage(content: String) {
        chatMessages.add(
            io.legado.app.data.entities.ChatMessage(
                content = content,
                isUser = false,
                isWelcome = true  // 标记为欢迎消息
            )
        )
        chatMessagesLiveData.postValue(chatMessages.toList())
        // 不保存欢迎消息到历史
    }
    
    /**
     * 添加加载消息
     */
    private fun addLoadingMessage() {
        chatMessages.add(
            io.legado.app.data.entities.ChatMessage(
                content = "",
                isUser = false,
                isLoading = true
            )
        )
        chatMessagesLiveData.postValue(chatMessages.toList())
    }
    
    /**
     * 移除加载消息
     */
    private fun removeLoadingMessage() {
        chatMessages.removeAll { it.isLoading }
        chatMessagesLiveData.postValue(chatMessages.toList())
    }
    
    /**
     * 添加错误消息
     */
    fun addErrorMessage(error: String) {
        removeLoadingMessage()
        addAIMessage("抱歉，推荐失败了：$error\n\n请检查网络连接或 API 配置，然后重试。")
    }
    
    /**
     * 请求书籍推荐
     */
    fun requestBookRecommendation(userInput: String) {
        val apiKey = io.legado.app.help.config.AppConfig.aiApiKey
        if (apiKey.isBlank()) {
            aiSearchErrorLiveData.postValue("请先在设置中配置 AI API Key")
            return
        }
        
        addLoadingMessage()
        aiSearchErrorLiveData.postValue("")
        
        execute {
            val provider = io.legado.app.help.config.AppConfig.aiProvider
            
            // 调用 AI 服务推荐书籍
            val response = io.legado.app.model.ai.AISummaryService.recommendBooks(
                provider = provider,
                apiKey = apiKey,
                userInput = userInput
            )
            
            // 移除加载消息并添加推荐结果
            removeLoadingMessage()
            addAIMessage("", response.recommendations)
            
        }.onError {
            aiSearchErrorLiveData.postValue(it.localizedMessage ?: "推荐失败")
        }
    }
    
    /**
     * 清空聊天记录
     */
    fun clearChatMessages() {
        chatMessages.clear()
        chatMessagesLiveData.postValue(emptyList())
        // 清除保存的历史
        context.putPrefString(chatHistoryKey, "")
    }

}
