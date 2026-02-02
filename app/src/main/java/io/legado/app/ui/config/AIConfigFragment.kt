package io.legado.app.ui.config

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import io.legado.app.R
import io.legado.app.lib.dialogs.alert
import io.legado.app.lib.prefs.fragment.PreferenceFragment
import io.legado.app.lib.theme.primaryColor
import io.legado.app.utils.setEdgeEffectColor

/**
 * AI 设置 Fragment
 */
class AIConfigFragment : PreferenceFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_config_ai)
        
        // 设置 API Key 输入框为密码类型
        findPreference<EditTextPreference>("aiApiKey")?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.ai_setting)
        listView.setEdgeEffectColor(primaryColor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // 可以在这里处理配置变化
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "ai_help_deepseek" -> {
                openUrl("https://platform.deepseek.com/")
                return true
            }
            "ai_help_glm" -> {
                openUrl("https://open.bigmodel.cn/")
                return true
            }
            "ai_help_gemini" -> {
                openUrl("https://ai.google.dev/")
                return true
            }
            "ai_feature_intro" -> {
                // 可以打开一个对话框显示功能介绍
                showFeatureIntro()
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showFeatureIntro() {
        alert(R.string.ai_feature_intro) {
            setMessage("""
                AI 章节摘要功能说明：
                
                1. 功能介绍
                • 为每个章节生成 100-200 字的智能摘要
                • 自动提取关键情节和人物
                • 支持多个 AI 服务商
                
                2. 支持的 AI 服务
                • DeepSeek：性价比高，推荐使用
                • GLM-4：完全免费
                • Gemini：Google 出品，质量稳定
                
                3. 使用方法
                • 在设置中启用 AI 功能
                • 选择 AI 服务商并输入 API Key
                • 阅读时点击菜单中的"AI 摘要"
                
                4. 隐私提示
                • 章节内容会发送到 AI 服务商
                • API Key 仅存储在本地
                • 建议只对公开书籍使用
            """.trimIndent())
            okButton()
        }
    }
}
