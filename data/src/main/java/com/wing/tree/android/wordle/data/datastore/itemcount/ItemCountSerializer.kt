package com.wing.tree.android.wordle.data.datastore.itemcount

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.wing.tree.android.wordle.domain.model.item.ItemCount as DomainItemCount
import java.io.InputStream
import java.io.OutputStream

object ItemCountSerializer : Serializer<ItemCount> {

    override val defaultValue: ItemCount = ItemCount.newBuilder()
        .setEraser(DomainItemCount.Default.ERASER)
        .setHint(DomainItemCount.Default.HINT)
        .setOneMoreTry(DomainItemCount.Default.ONE_MORE_TRY)
        .build()

    override suspend fun readFrom(input: InputStream): ItemCount {
        try {
            return ItemCount.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ItemCount, output: OutputStream) = t.writeTo(output)
}