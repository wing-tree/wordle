package com.wing.tree.android.wordle.data.datastore.statistics

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import java.io.InputStream
import java.io.OutputStream

object StatisticsSerializer : Serializer<Statistics> {
    override val defaultValue: Statistics = Statistics.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Statistics {
        try {
            return Statistics.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Statistics, output: OutputStream) = t.writeTo(output)
}