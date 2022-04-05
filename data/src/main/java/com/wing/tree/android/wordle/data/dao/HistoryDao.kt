package com.wing.tree.android.wordle.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wing.tree.android.wordle.data.entity.History
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: History)

    @Query("SELECT * FROM history WHERE date IS :date")
    fun get(date: Date): Flow<History>
}