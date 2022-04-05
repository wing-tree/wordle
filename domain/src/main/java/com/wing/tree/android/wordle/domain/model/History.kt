package com.wing.tree.android.wordle.domain.model

import java.util.*

interface History {
    val date: Date
    val indices: List<Int>
}