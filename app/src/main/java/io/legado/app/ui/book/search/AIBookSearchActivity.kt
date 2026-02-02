package io.legado.app.ui.book.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.legado.app.R
import io.legado.app.base.VMBaseActivity
import io.legado.app.constant.Theme
import io.legado.app.databinding.ActivityAiBookSearchBinding
import io.legado.app.lib.theme.primaryColor
import io.legado.app.utils.hideSoftInput
import io.legado.app.utils.showSoftInput
import io.legado.app.utils.startActivity
import io.legado.app.utils.viewbindingdelegate.viewBinding

/**
 * AI 智能找书页面
 */
class AIBookSearchActivity : VMBaseActivity<ActivityAiBookSearchBinding, SearchViewModel>(
    fullScreen = false,
    theme = Theme.Auto,
    toolBarTheme = Theme.Auto
) {

    override val binding by viewBinding(ActivityAiBookSearchBinding::inflate)
    override val viewModel by viewModels<SearchViewModel>()
    
    private val adapter by lazy {
        ChatMessageAdapter(this) { bookTitle, bookAuthor ->
            // 点击推荐书籍，自动搜索
            searchBook(bookTitle, bookAuthor)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setupSystemBar()
        setupWindowInsets()
        initRecyclerView()
        initInputArea()
        observeData()
        
        // 加载历史对话
        loadChatHistory()
    }

    override fun setupSystemBar() {
        super.setupSystemBar()
        // 确保状态栏使用主题颜色
        binding.titleBar.setBackgroundColor(primaryColor)
    }

    /**
     * 设置窗口插入监听，让输入框跟随键盘
     */
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // 设置输入区域的底部边距，跟随键盘
            binding.llInputArea.setPadding(
                binding.llInputArea.paddingLeft,
                binding.llInputArea.paddingTop,
                binding.llInputArea.paddingRight,
                if (imeInsets.bottom > 0) imeInsets.bottom else systemBarsInsets.bottom
            )
            
            insets
        }
    }

    override fun onCompatCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.ai_book_search, menu)
        return super.onCompatCreateOptionsMenu(menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_history -> {
                // 清空历史
                viewModel.clearChatMessages()
                showWelcomeMessage()
                return true
            }
        }
        return super.onCompatOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initInputArea() {
        // 发送按钮点击
        binding.fabSend.setOnClickListener {
            sendMessage()
        }
        
        // 输入框回车发送
        binding.editInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
        
        // 自动弹出键盘
        binding.editInput.postDelayed({
            binding.editInput.requestFocus()
            binding.editInput.showSoftInput()
        }, 200)
    }

    private fun observeData() {
        // 观察聊天消息
        viewModel.chatMessagesLiveData.observe(this) { messages ->
            adapter.setMessages(messages)
            // 滚动到最新消息
            if (messages.isNotEmpty()) {
                binding.recyclerView.smoothScrollToPosition(messages.size - 1)
            }
        }
        
        // 观察错误信息
        viewModel.aiSearchErrorLiveData.observe(this) { error ->
            if (error.isNotEmpty()) {
                // 显示错误消息
                viewModel.addErrorMessage(error)
            }
        }
    }

    private fun loadChatHistory() {
        // 加载历史对话
        viewModel.loadChatHistory()
        
        // 延迟检查，等待加载完成
        binding.recyclerView.postDelayed({
            if (viewModel.chatMessagesLiveData.value.isNullOrEmpty()) {
                showWelcomeMessage()
            }
        }, 100)
    }

    private fun showWelcomeMessage() {
        val welcomeMessage = """
你好！我是 AI 找书助手

告诉我你想看什么类型的书，我会为你推荐合适的小说。

例如：
• 我想看玄幻升级流小说
• 推荐一些都市爽文
• 有没有好看的历史架空小说
        """.trimIndent()
        
        // 添加欢迎消息，但不保存到历史
        viewModel.addWelcomeMessage(welcomeMessage)
    }

    private fun sendMessage() {
        val input = binding.editInput.text.toString().trim()
        if (input.isEmpty()) {
            return
        }
        
        // 清空输入框
        binding.editInput.setText("")
        binding.editInput.hideSoftInput()
        
        // 添加用户消息
        viewModel.addUserMessage(input)
        
        // 请求 AI 推荐
        viewModel.requestBookRecommendation(input)
    }

    private fun searchBook(bookTitle: String, bookAuthor: String) {
        // 返回搜索界面并触发搜索
        val searchKey = "$bookTitle $bookAuthor"
        val intent = Intent().apply {
            putExtra("searchKey", searchKey)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity<AIBookSearchActivity>()
        }
    }
}
