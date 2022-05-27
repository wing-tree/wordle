package com.wing.tree.android.wordle.presentation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.wing.tree.android.wordle.presentation.R
import java.io.File
import java.io.FileOutputStream
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

fun Activity.captureScreen(onCompressed: (Uri) -> Unit, onFailure: (Exception) -> Unit) {
    val authority = "$packageName.fileprovider"
    val file = File(filesDir, "screen.png")

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        pixelCopy ({ bitmap ->
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)

                val uri = FileProvider.getUriForFile(this, authority, file)

                onCompressed(uri)
            }
        }) {
            onFailure(it)
        }
    } else {
        @Suppress("DEPRECATION")
        with(window.decorView.rootView) {
            isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(drawingCache)
            isDrawingCacheEnabled = false

            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)

                val uri = FileProvider.getUriForFile(this@captureScreen, authority, file)

                onCompressed(uri)
            }
        }
    }
}

@RequiresApi(android.os.Build.VERSION_CODES.O)
private fun Activity.pixelCopy(onPixelCopyFinished: (Bitmap) -> Unit, onFailure: (Exception) -> Unit) {
    with(window.decorView.rootView) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val outLocation = IntArray(2)

        getLocationInWindow(outLocation)

        val rect = Rect(outLocation[0], outLocation[1], outLocation[0] + width, outLocation[1] + height)

        try {
            PixelCopy.request(window, rect, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    onPixelCopyFinished(bitmap)
                } else {
                    onFailure(IllegalStateException("$copyResult"))
                }
            }, Handler(Looper.getMainLooper()))
        } catch (exception: IllegalArgumentException) {
            onFailure(exception)
        }
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun Activity.sendPngImage(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(intent, null)

    packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY).forEach { resolveInfo ->
        val packageName = resolveInfo.activityInfo.packageName
        val modeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        grantUriPermission(packageName, uri, modeFlags)
    }

    startActivity(chooser)
}