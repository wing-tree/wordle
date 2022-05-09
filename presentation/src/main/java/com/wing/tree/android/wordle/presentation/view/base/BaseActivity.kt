package com.wing.tree.android.wordle.presentation.view.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar

abstract class BaseActivity<VB: ViewBinding>  : AppCompatActivity() {
    abstract fun inflate(): VB
    abstract fun initData()
    abstract fun bind(viewBinding: VB)

    protected val viewBinding: VB by lazy { inflate() }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        hideSystemUi()
        initData()
        bind(viewBinding)
    }

    private fun hideSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                val flag = WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()

                it.hide(flag)
                it.systemBarsBehavior =  WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            val visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = visibility
        }
    }

    protected fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }
}