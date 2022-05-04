package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.playstate.PlayState
import kotlinx.coroutines.flow.Flow

interface PlayStateRepository {
    fun get(): Flow<PlayState>
    suspend fun clear()
    suspend fun update(playState: PlayState)
}