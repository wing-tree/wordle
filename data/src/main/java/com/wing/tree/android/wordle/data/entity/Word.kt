package com.wing.tree.android.wordle.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wing.tree.android.wordle.domain.model.Word

@Entity(tableName = "word")
class Word(
    @PrimaryKey(autoGenerate = true)
    override val index: Int = 0,
    override val word: String
) : Word