package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.constant.Duration
import com.wing.tree.android.wordle.presentation.databinding.KeyboardViewBinding
import com.wing.tree.android.wordle.presentation.extention.scaleUpDown
import com.wing.tree.android.wordle.presentation.model.play.Key
import com.wing.tree.android.wordle.presentation.model.play.Keyboard

class KeyboardView : LinearLayout {
    private val viewBinding: KeyboardViewBinding = KeyboardViewBinding.bind(inflate(context, R.layout.keyboard_view, this))

    private var onKeyListener: OnKeyListener? = null

    fun interface OnKeyListener {
        fun onKey(key: Key)
    }

    init {
        gravity = Gravity.CENTER
        orientation = VERTICAL
    }

    constructor(context: Context) : super(context) {
        bind()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        bind()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        bind()
    }

    private fun bind() {
        val onClickListener = OnClickListener { view ->
            val key = when(view.id) {
                R.id.image_view_keyboard_backspace -> Key.Backspace
                R.id.image_view_keyboard_return -> Key.Return
                R.id.key_view_a -> Key.Alphabet("a")
                R.id.key_view_b -> Key.Alphabet("b")
                R.id.key_view_c -> Key.Alphabet("c")
                R.id.key_view_d -> Key.Alphabet("d")
                R.id.key_view_e -> Key.Alphabet("e")
                R.id.key_view_f -> Key.Alphabet("f")
                R.id.key_view_g -> Key.Alphabet("g")
                R.id.key_view_h -> Key.Alphabet("h")
                R.id.key_view_i -> Key.Alphabet("i")
                R.id.key_view_j -> Key.Alphabet("j")
                R.id.key_view_k -> Key.Alphabet("k")
                R.id.key_view_l -> Key.Alphabet("l")
                R.id.key_view_m -> Key.Alphabet("m")
                R.id.key_view_n -> Key.Alphabet("n")
                R.id.key_view_o -> Key.Alphabet("o")
                R.id.key_view_p -> Key.Alphabet("p")
                R.id.key_view_q -> Key.Alphabet("q")
                R.id.key_view_r -> Key.Alphabet("r")
                R.id.key_view_s -> Key.Alphabet("s")
                R.id.key_view_t -> Key.Alphabet("t")
                R.id.key_view_u -> Key.Alphabet("u")
                R.id.key_view_v -> Key.Alphabet("v")
                R.id.key_view_w -> Key.Alphabet("w")
                R.id.key_view_x -> Key.Alphabet("x")
                R.id.key_view_y -> Key.Alphabet("y")
                R.id.key_view_z -> Key.Alphabet("z")
                else -> throw IllegalArgumentException("${view.id}")
            }

            view?.scaleUpDown()

            onKeyListener?.onKey(key)
        }

        with(viewBinding) {
            imageViewKeyboardBackspace.setOnClickListener(onClickListener)
            imageViewKeyboardReturn.setOnClickListener(onClickListener)
            keyViewA.setOnClickListener(onClickListener)
            keyViewB.setOnClickListener(onClickListener)
            keyViewC.setOnClickListener(onClickListener)
            keyViewD.setOnClickListener(onClickListener)
            keyViewE.setOnClickListener(onClickListener)
            keyViewF.setOnClickListener(onClickListener)
            keyViewG.setOnClickListener(onClickListener)
            keyViewH.setOnClickListener(onClickListener)
            keyViewI.setOnClickListener(onClickListener)
            keyViewJ.setOnClickListener(onClickListener)
            keyViewK.setOnClickListener(onClickListener)
            keyViewL.setOnClickListener(onClickListener)
            keyViewM.setOnClickListener(onClickListener)
            keyViewN.setOnClickListener(onClickListener)
            keyViewO.setOnClickListener(onClickListener)
            keyViewP.setOnClickListener(onClickListener)
            keyViewQ.setOnClickListener(onClickListener)
            keyViewR.setOnClickListener(onClickListener)
            keyViewS.setOnClickListener(onClickListener)
            keyViewT.setOnClickListener(onClickListener)
            keyViewU.setOnClickListener(onClickListener)
            keyViewV.setOnClickListener(onClickListener)
            keyViewW.setOnClickListener(onClickListener)
            keyViewX.setOnClickListener(onClickListener)
            keyViewY.setOnClickListener(onClickListener)
            keyViewZ.setOnClickListener(onClickListener)
        }
    }

    private fun findViewByKey(key: String): KeyView? {
        return with(viewBinding) {
            when (key.lowercase()) {
                "a" -> keyViewA
                "b" -> keyViewB
                "c" -> keyViewC
                "d" -> keyViewD
                "e" -> keyViewE
                "f" -> keyViewF
                "g" -> keyViewG
                "h" -> keyViewH
                "i" -> keyViewI
                "j" -> keyViewJ
                "k" -> keyViewK
                "l" -> keyViewL
                "m" -> keyViewM
                "n" -> keyViewN
                "o" -> keyViewO
                "p" -> keyViewP
                "q" -> keyViewQ
                "r" -> keyViewR
                "s" -> keyViewS
                "t" -> keyViewT
                "u" -> keyViewU
                "v" -> keyViewV
                "w" -> keyViewW
                "x" -> keyViewX
                "y" -> keyViewY
                "z" -> keyViewZ
                else -> null
            }
        }
    }

    fun disable() {
        isEnabled = false
    }

    fun enable() {
        isEnabled = true
    }

    fun submitKeyboard(keyboard: Keyboard, commitCallback: () -> Unit) {
        with(keyboard) {
            alphabets.forEach {
                findViewByKey(it.letter)?.updateState(it.state, runsAnimation.get())
            }
        }

        commitCallback()
    }

    fun setOnKeyListener(onKeyListener: OnKeyListener) {
        this.onKeyListener = onKeyListener
    }
}