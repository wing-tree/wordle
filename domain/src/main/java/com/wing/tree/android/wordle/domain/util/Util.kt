package com.wing.tree.android.wordle.domain.util

import java.lang.ref.WeakReference
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@OptIn(ExperimentalContracts::class)
fun Any?.isNull(): Boolean {
    contract { returns(true) implies (this@isNull == null) }

    return this == null
}

@OptIn(ExperimentalContracts::class)
fun Any?.notNull(): Boolean {
    contract { returns(true) implies (this@notNull != null) }

    return this != null
}

val Int.float get() = toFloat()

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