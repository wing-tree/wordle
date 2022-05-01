package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.KeyboardViewBinding
import com.wing.tree.android.wordle.presentation.model.play.Key
import com.wing.tree.android.wordle.presentation.model.play.Letter

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
        val onClickListener = OnClickListener {
            val key = when(it.id) {
                R.id.key_a -> Key.Alphabet("a")
                R.id.key_b -> Key.Alphabet("b")
                R.id.key_c -> Key.Alphabet("c")
                R.id.key_d -> Key.Alphabet("d")
                R.id.key_e -> Key.Alphabet("e")
                R.id.key_f -> Key.Alphabet("f")
                R.id.key_g -> Key.Alphabet("g")
                R.id.key_h -> Key.Alphabet("h")
                R.id.key_i -> Key.Alphabet("i")
                R.id.key_j -> Key.Alphabet("j")
                R.id.key_k -> Key.Alphabet("k")
                R.id.key_l -> Key.Alphabet("l")
                R.id.key_m -> Key.Alphabet("m")
                R.id.key_n -> Key.Alphabet("n")
                R.id.key_o -> Key.Alphabet("o")
                R.id.key_p -> Key.Alphabet("p")
                R.id.key_q -> Key.Alphabet("q")
                R.id.key_r -> Key.Alphabet("r")
                R.id.key_s -> Key.Alphabet("s")
                R.id.key_t -> Key.Alphabet("t")
                R.id.key_u -> Key.Alphabet("u")
                R.id.key_v -> Key.Alphabet("v")
                R.id.key_w -> Key.Alphabet("w")
                R.id.key_x -> Key.Alphabet("x")
                R.id.key_y -> Key.Alphabet("y")
                R.id.key_z -> Key.Alphabet("z")
                R.id.key_return -> Key.Return
                R.id.key_backspace -> Key.Backspace
                else -> throw IllegalArgumentException("${it.id}")
            }

            onKeyListener?.onKey(key)
        }

        with(viewBinding) {
            keyA.setOnClickListener(onClickListener)
            keyB.setOnClickListener(onClickListener)
            keyC.setOnClickListener(onClickListener)
            keyD.setOnClickListener(onClickListener)
            keyE.setOnClickListener(onClickListener)
            keyF.setOnClickListener(onClickListener)
            keyG.setOnClickListener(onClickListener)
            keyH.setOnClickListener(onClickListener)
            keyI.setOnClickListener(onClickListener)
            keyJ.setOnClickListener(onClickListener)
            keyK.setOnClickListener(onClickListener)
            keyL.setOnClickListener(onClickListener)
            keyM.setOnClickListener(onClickListener)
            keyN.setOnClickListener(onClickListener)
            keyO.setOnClickListener(onClickListener)
            keyP.setOnClickListener(onClickListener)
            keyQ.setOnClickListener(onClickListener)
            keyR.setOnClickListener(onClickListener)
            keyS.setOnClickListener(onClickListener)
            keyT.setOnClickListener(onClickListener)
            keyU.setOnClickListener(onClickListener)
            keyV.setOnClickListener(onClickListener)
            keyW.setOnClickListener(onClickListener)
            keyX.setOnClickListener(onClickListener)
            keyY.setOnClickListener(onClickListener)
            keyZ.setOnClickListener(onClickListener)
            keyReturn.setOnClickListener(onClickListener)
            keyBackspace.setOnClickListener(onClickListener)
        }
    }

    private fun findViewByKey(key: String): KeyView? {
        return with(viewBinding) {
            when (key.lowercase()) {
                "a" -> keyA
                "b" -> keyB
                "c" -> keyC
                "d" -> keyD
                "e" -> keyE
                "f" -> keyF
                "g" -> keyG
                "h" -> keyH
                "i" -> keyI
                "j" -> keyJ
                "k" -> keyK
                "l" -> keyL
                "m" -> keyM
                "n" -> keyN
                "o" -> keyO
                "p" -> keyP
                "q" -> keyQ
                "r" -> keyR
                "s" -> keyS
                "t" -> keyT
                "u" -> keyU
                "v" -> keyV
                "w" -> keyW
                "x" -> keyX
                "y" -> keyY
                "z" -> keyZ
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

    fun applyState(letters: Array<Key.Alphabet>) {
        letters.forEach {
            findViewByKey(it.letter)?.updateState(it.state)
        }
    }

    fun setOnKeyListener(onKeyListener: OnKeyListener) {
        this.onKeyListener = onKeyListener
    }
}