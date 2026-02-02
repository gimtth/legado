package io.legado.app.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.parcelize.Parcelize

/**
 * AI 章节摘要实体
 */
@Parcelize
@Entity(
    tableName = "chapter_summaries",
    primaryKeys = ["bookUrl", "chapterUrl"],
    indices = [Index(value = ["bookUrl"], unique = false)]
)
data class ChapterSummary(
    var bookUrl: String = "",           // 书籍地址
    var chapterUrl: String = "",        // 章节地址
    var summary: String = "",           // AI 生成的摘要
    var aiProvider: String = "",        // 使用的 AI 服务（deepseek/glm/gemini）
    var createTime: Long = System.currentTimeMillis()  // 生成时间
) : Parcelable
