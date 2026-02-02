package io.legado.app.ui.book.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import io.legado.app.R
import io.legado.app.data.entities.AIBookRecommendation
import io.legado.app.data.entities.ChatMessage
import io.legado.app.lib.theme.accentColor
import io.legado.app.utils.ColorUtils
import io.legado.app.utils.dpToPx

/**
 * 聊天消息适配器
 */
class ChatMessageAdapter(
    private val context: Context,
    private val onBookClick: (title: String, author: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()
    
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_ai_message_user, parent, false)
                UserMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_ai_message_ai, parent, false)
                AIMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AIMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    /**
     * 用户消息 ViewHolder
     */
    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)

        fun bind(message: ChatMessage) {
            tvMessage.text = message.content
        }
    }

    /**
     * AI 消息 ViewHolder
     */
    inner class AIMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        private val llBooks: LinearLayout = itemView.findViewById(R.id.ll_books)
        private val llLoading: LinearLayout = itemView.findViewById(R.id.ll_loading)

        fun bind(message: ChatMessage) {
            // 显示加载状态
            if (message.isLoading) {
                tvMessage.visibility = View.GONE
                llBooks.visibility = View.GONE
                llLoading.visibility = View.VISIBLE
                return
            }
            
            llLoading.visibility = View.GONE
            
            // 显示文本消息
            if (message.content.isNotEmpty()) {
                tvMessage.visibility = View.VISIBLE
                tvMessage.text = message.content
            } else {
                tvMessage.visibility = View.GONE
            }
            
            // 显示推荐书籍
            if (!message.recommendations.isNullOrEmpty()) {
                llBooks.visibility = View.VISIBLE
                llBooks.removeAllViews()
                
                message.recommendations.forEach { book ->
                    val bookView = createBookRecommendationView(book)
                    llBooks.addView(bookView)
                }
            } else {
                llBooks.visibility = View.GONE
            }
        }

        private fun createBookRecommendationView(book: AIBookRecommendation): View {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_ai_book_recommendation, llBooks, false)
            
            val tvTitle = view.findViewById<TextView>(R.id.tv_book_title)
            val tvAuthor = view.findViewById<TextView>(R.id.tv_book_author)
            val tvReason = view.findViewById<TextView>(R.id.tv_reason)
            val flexboxTags = view.findViewById<FlexboxLayout>(R.id.flexbox_tags)
            
            tvTitle.text = book.title
            tvAuthor.text = book.author
            tvReason.text = book.reason
            
            // 添加标签
            flexboxTags.removeAllViews()
            book.tags.forEach { tag ->
                val tagView = createTagView(tag)
                flexboxTags.addView(tagView)
            }
            
            // 点击事件
            view.setOnClickListener {
                onBookClick(book.title, book.author)
            }
            
            return view
        }

        private fun createTagView(tag: String): TextView {
            return TextView(context).apply {
                text = tag
                textSize = 11f
                setTextColor(context.accentColor)
                setBackgroundResource(R.drawable.shape_fillet_btn)
                val padding = 6.dpToPx()
                setPadding(padding, padding / 2, padding, padding / 2)
                
                val margin = 4.dpToPx()
                layoutParams = FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, margin, margin)
                }
            }
        }
    }
}
