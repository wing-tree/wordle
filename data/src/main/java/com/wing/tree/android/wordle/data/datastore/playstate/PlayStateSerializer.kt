package com.wing.tree.android.wordle.data.datastore.playstate

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.wing.tree.android.wordle.domain.model.playstate.Letter.State.UNKNOWN
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import java.io.InputStream
import java.io.OutputStream

object PlayStateSerializer : Serializer<PlayState> {
    override val defaultValue: PlayState
        get() = run {
            val builder = PlayState.newBuilder()

            builder.word = Word.getDefaultInstance()
            builder.playBoard = defaultPlayBoard

            builder.build()
        }

    private val defaultKeyboard: Keyboard = Keyboard.newBuilder().apply {

    }.build()

    private val defaultPlayBoard: PlayBoard = PlayBoard.newBuilder().apply {
        round = 0
        maximumRound = MAXIMUM_ROUND
        addAllLine(List(MAXIMUM_ROUND) { getDefaultLine(it) })
    }.build()

    private fun getDefaultLine(round: Int): Line = Line.newBuilder().apply {
        this.round = round

        addAllLetter(List<Letter>(WORD_LENGTH) { getDefaultLetter(it) })
        addAllPreviousLetter(List<Letter>(WORD_LENGTH) { getDefaultLetter(it) })

        isSubmitted = false
    }.build()

    private fun getDefaultLetter(position: Int) = Letter.newBuilder().apply {
        this.position = position
        value = BLANK
        state = UNKNOWN
    }.build()

    override suspend fun readFrom(input: InputStream): PlayState {
        try {
            return PlayState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: PlayState, output: OutputStream) = t.writeTo(output)
}