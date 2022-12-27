package com.wing.tree.android.wordle.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wing.tree.android.wordle.data.entity.Word
import kotlinx.coroutines.flow.Flow


@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(words: List<Word>)

    @Query("SELECT COUNT(`index`) FROM word")
    suspend fun count(): Int

    @Query("SELECT * FROM word WHERE `index` IS :index")
    suspend fun get(index: Int): Word

    @Query("SELECT * FROM word WHERE value IS :letters")
    suspend fun get(letters: String): Word?

    @Query("SELECT * FROM word ORDER BY RANDOM() LIMIT 1")
    suspend fun random(): Word
}