package com.wing.tree.android.wordle.presentation.util

import android.content.Context
import android.content.Intent
import com.wing.tree.android.wordle.presentation.R
import java.util.concurrent.atomic.AtomicInteger

internal fun shareApp(context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
    val text = "https://play.google.com/store/apps/details?id=${context.packageName}"

    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)

    Intent.createChooser(intent, context.getString(R.string.share_the_app)).also {
        it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(it)
    }
}

fun AtomicInteger.increment() {
    set(get().inc())
}