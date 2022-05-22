package com.wing.tree.android.wordle.presentation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
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

inline fun <reified T: Activity> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T: Activity> Fragment.startActivity() {
    startActivity(Intent(requireContext(), T::class.java))
}