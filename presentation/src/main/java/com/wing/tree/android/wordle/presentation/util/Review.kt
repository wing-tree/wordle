package com.wing.tree.android.wordle.presentation.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.wing.tree.android.wordle.presentation.R
import kotlinx.coroutines.launch
import timber.log.Timber

object Review {
    fun launchReviewFlow(activity: FragmentActivity) {
        val reviewManager = ReviewManagerFactory.create(activity)
        val task = reviewManager.requestReviewFlow()

        task.addOnCompleteListener {
            if (it.isSuccessful) {
                val reviewInfo = it.result

                reviewInfo.describeContents()
                reviewManager.launchReviewFlow(activity, reviewInfo).apply {
                    addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            activity.lifecycleScope.launch {
                                val text = activity.getString(R.string.review_000)

                                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Timber.e(task.exception)
                goToPlayStore(activity)
            }
        }
    }

    private fun goToPlayStore(context: Context) {
        try {
            context.startActivity(
                Intent (
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${context.packageName}"))
            )
        } catch (activityNotFoundException: ActivityNotFoundException) {
            Timber.e(activityNotFoundException)
            context.startActivity(
                Intent (
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                )
            )
        }
    }
}