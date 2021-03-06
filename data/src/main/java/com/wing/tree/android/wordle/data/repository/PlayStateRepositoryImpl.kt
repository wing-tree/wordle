package com.wing.tree.android.wordle.data.repository

import androidx.datastore.core.DataStore
import com.wing.tree.android.wordle.data.datastore.playstate.PlayState
import com.wing.tree.android.wordle.data.datastore.playstate.PlayStateSerializer
import com.wing.tree.android.wordle.data.mapper.toDataModel
import com.wing.tree.android.wordle.data.mapper.toDomainModel
import com.wing.tree.android.wordle.domain.repository.PlayStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.wing.tree.android.wordle.domain.model.playstate.PlayState as DomainPlayState

class PlayStateRepositoryImpl @Inject constructor(private val dataStore: DataStore<PlayState>) : PlayStateRepository {
    override fun get(): Flow<DomainPlayState> {
        return dataStore.data.map { it.toDomainModel() }
    }

    override suspend fun clear() {
        dataStore.updateData {
            PlayStateSerializer.defaultValue
        }
    }

    override suspend fun update(playState: DomainPlayState) {
        val keyboard = playState.keyboard.toDataModel()
        val playBoard = playState.playBoard.toDataModel()
        val word = playState.word.toDataModel()

        dataStore.updateData {
            it.toBuilder()
                .setKeyboard(keyboard)
                .setPlayBoard(playBoard)
                .setWord(word)
                .build()
        }
    }
}