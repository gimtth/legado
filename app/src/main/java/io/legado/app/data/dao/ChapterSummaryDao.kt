package io.legado.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.legado.app.data.entities.ChapterSummary

@Dao
interface ChapterSummaryDao {
    
    @Query("SELECT * FROM chapter_summaries WHERE bookUrl = :bookUrl AND chapterUrl = :chapterUrl")
    fun get(bookUrl: String, chapterUrl: String): ChapterSummary?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(summary: ChapterSummary)
    
    @Query("DELETE FROM chapter_summaries WHERE bookUrl = :bookUrl")
    fun deleteByBook(bookUrl: String)
    
    @Query("DELETE FROM chapter_summaries WHERE bookUrl = :bookUrl AND chapterUrl = :chapterUrl")
    fun delete(bookUrl: String, chapterUrl: String)
    
    @Query("SELECT COUNT(*) FROM chapter_summaries WHERE bookUrl = :bookUrl")
    fun getCountByBook(bookUrl: String): Int
}
