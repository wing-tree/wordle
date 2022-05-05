package com.wing.tree.android.wordle.data.datastore.playstate

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.wing.tree.android.wordle.domain.model.playstate.Key
import com.wing.tree.android.wordle.domain.model.playstate.Letter.State.UNDEFINED
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import com.wing.tree.wordle.core.constant.alphabet
import java.io.InputStream
import java.io.OutputStream

object PlayStateSerializer : Serializer<PlayState> {
    override val defaultValue: PlayState
        get() = PlayState.newBuilder()
        .setKeyboard(defaultKeyboard)
        .setPlayBoard(defaultPlayBoard)
        .build()

    private val defaultKeyboard: Keyboard = Keyboard.newBuilder()
        .addAllAlphabet(List(alphabet.size) { getDefaultAlphabetKey(alphabet[it]) })
        .build()

    private fun getDefaultAlphabetKey(letter: String): AlphabetKey = AlphabetKey.newBuilder()
        .setLetter(letter)
        .setState(Key.Alphabet.State.UNDEFINED)
        .build()

    private val defaultPlayBoard: PlayBoard = PlayBoard.newBuilder()
        .setRound(0)
        .setMaximumRound(MAXIMUM_ROUND)
        .addAllLine(List(MAXIMUM_ROUND) { getDefaultLine(it) })
        .build()

    private fun getDefaultLine(round: Int): Line = Line.newBuilder()
        .setRound(round)
        .addAllLetter(List<Letter>(WORD_LENGTH) { getDefaultLetter(it) })
        .addAllPreviousLetter(List<Letter>(WORD_LENGTH) { getDefaultLetter(it) })
        .setIsSubmitted(false)
        .build()

    private fun getDefaultLetter(position: Int) = Letter.newBuilder()
        .setPosition(position)
        .setValue(BLANK)
        .setState(UNDEFINED)
        .build()

    override suspend fun readFrom(input: InputStream): PlayState {
        try {
            return PlayState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: PlayState, output: OutputStream) = t.writeTo(output)
}