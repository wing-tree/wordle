package com.wing.tree.android.wordle.data.datastore.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings.newBuilder()
        .setIsHardMode(false)
        .setVibrates(true)
        .setIsHighContrastMode(false)
        .build()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            @Suppress("BlockingMethodInNonBlockingContext")
            return Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)
}