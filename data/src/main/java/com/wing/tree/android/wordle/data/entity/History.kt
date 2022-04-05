package com.wing.tree.android.wordle.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wing.tree.android.wordle.domain.model.History
import java.util.*

@Entity(tableName = "history")
data class History(
    @PrimaryKey
    override val date: Date,
    override val indices: List<Int>
) : History