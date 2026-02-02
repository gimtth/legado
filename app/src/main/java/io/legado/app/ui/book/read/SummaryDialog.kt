package io.legado.app.ui.book.read

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import io.legado.app.R
import io.legado.app.base.BaseDialogFragment
import io.legado.app.databinding.DialogChapterSummaryBinding
import io.legado.app.lib.theme.primaryColor
import io.legado.app.utils.setLayout
import io.legado.app.utils.viewbindingdelegate.viewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AI 章节摘要对话框
 */
class SummaryDialog : BaseDialogFragment(R.layout.dialog_chapter_summary) {

    private val binding by viewBinding(DialogChapterSummaryBinding::bind)
    private val viewModel by activityViewModels<ReadBookViewModel>()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onStart() {
        super.onStart()
        setLayout(0.9f, 0.8f)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        // 设置工具栏颜色
        binding.toolBar.setBackgroundColor(primaryColor)
        
        // 初始化按钮事件
        initButtons()
        
        // 观察数据变化
        observeData()
        
        // 加载缓存的摘要
        viewModel.loadCachedSummary()
    }

    private fun initButtons() {
        // 生成按钮
        binding.tvGenerate.setOnClickListener {
            viewModel.generateChapterSummary()
        }
        
        // 重新生成按钮
        binding.tvRegenerate.setOnClickListener {
            viewModel.regenerateChapterSummary()
        }
        
        // 关闭按钮
        binding.tvClose.setOnClickListener {
            dismiss()
        }
    }

    private fun observeData() {
        // 观察摘要数据
        viewModel.chapterSummaryLiveData.observe(viewLifecycleOwner) { summary ->
            if (summary != null) {
                showSummary(summary)
            } else {
                showEmpty()
            }
        }
        
        // 观察加载状态
        viewModel.summaryLoadingLiveData.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                showLoading()
            }
        }
        
        // 观察错误信息
        viewModel.summaryErrorLiveData.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                showError(error)
            }
        }
    }

    /**
     * 显示摘要内容
     */
    private fun showSummary(summary: io.legado.app.data.entities.ChapterSummary) {
        binding.llLoading.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
        binding.llEmpty.visibility = View.GONE
        binding.llError.visibility = View.GONE
        
        // 显示/隐藏按钮
        binding.tvGenerate.visibility = View.GONE
        binding.tvRegenerate.visibility = View.VISIBLE
        
        // 设置章节标题
        val chapter = io.legado.app.model.ReadBook.curTextChapter?.chapter
        binding.tvChapterTitle.text = chapter?.title ?: "当前章节"
        
        // 设置摘要内容
        binding.tvSummary.text = summary.summary
        
        // 设置 AI 服务商标识
        val providerName = when (summary.aiProvider.lowercase()) {
            "deepseek" -> "DeepSeek"
            "glm" -> "GLM-4"
            "gemini" -> "Gemini"
            else -> summary.aiProvider
        }
        binding.tvAiProvider.text = providerName
        
        // 设置生成时间
        val timeStr = dateFormat.format(Date(summary.createTime))
        binding.tvCreateTime.text = "生成于 $timeStr"
    }

    /**
     * 显示空状态
     */
    private fun showEmpty() {
        binding.llLoading.visibility = View.GONE
        binding.scrollView.visibility = View.GONE
        binding.llEmpty.visibility = View.VISIBLE
        binding.llError.visibility = View.GONE
        
        binding.tvGenerate.visibility = View.VISIBLE
        binding.tvRegenerate.visibility = View.GONE
        
        binding.tvEmptyText.text = "暂无摘要"
    }

    /**
     * 显示加载状态
     */
    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
        binding.scrollView.visibility = View.GONE
        binding.llEmpty.visibility = View.GONE
        binding.llError.visibility = View.GONE
        
        binding.tvGenerate.visibility = View.GONE
        binding.tvRegenerate.visibility = View.GONE
        
        binding.tvLoadingText.text = "AI 正在生成摘要..."
    }

    /**
     * 显示错误状态
     */
    private fun showError(error: String) {
        binding.llLoading.visibility = View.GONE
        binding.scrollView.visibility = View.GONE
        binding.llEmpty.visibility = View.GONE
        binding.llError.visibility = View.VISIBLE
        
        binding.tvGenerate.visibility = View.VISIBLE
        binding.tvRegenerate.visibility = View.GONE
        
        binding.tvErrorText.text = error
    }
}
