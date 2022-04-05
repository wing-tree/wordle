package com.wing.tree.android.wordle.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.wing.tree.android.wordle.data.entity.Word
import kotlinx.coroutines.flow.Flow


@Dao
interface WordDao {
    @Query("SELECT COUNT(`index`) FROM word")
    fun count(): Flow<Int>

    @Query("SELECT * FROM word WHERE `index` IS :index")
    fun get(index: Int): Flow<Word>
}