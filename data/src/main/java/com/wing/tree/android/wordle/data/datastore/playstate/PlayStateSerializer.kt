package com.wing.tree.android.wordle.data.datastore.playstate

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object PlayStateSerializer : Serializer<PlayState> {
    override val defaultValue: PlayState = PlayState.newBuilder()
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