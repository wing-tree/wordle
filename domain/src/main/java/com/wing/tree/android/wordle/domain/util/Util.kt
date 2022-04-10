package com.wing.tree.android.wordle.domain.util

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

val Any?.isNull get() = this == null
val Any?.notNull: Boolean get() = isNull.not()

fun <T> weakReference(referent: T? = null): ReadWriteProperty<Any?, T?> {
    return object : ReadWriteProperty<Any?, T?> {
        var weakReference = WeakReference<T?>(referent)
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return weakReference.get()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            weakReference = WeakReference(value)
        }
    }
}