package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.KeyboardBinding

class Keyboard : LinearLayout {
    private val viewBinding: KeyboardBinding = KeyboardBinding.bind(inflate(context, R.layout.keyboard, this))

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
                R.id.key_a -> Key.Alphabet("A")
                R.id.key_b -> Key.Alphabet("B")
                R.id.key_c -> Key.Alphabet("C")
                R.id.key_d -> Key.Alphabet("D")
                R.id.key_e -> Key.Alphabet("E")
                R.id.key_f -> Key.Alphabet("F")
                R.id.key_g -> Key.Alphabet("G")
                R.id.key_h -> Key.Alphabet("H")
                R.id.key_i -> Key.Alphabet("I")
                R.id.key_j -> Key.Alphabet("J")
                R.id.key_k -> Key.Alphabet("K")
                R.id.key_l -> Key.Alphabet("L")
                R.id.key_m -> Key.Alphabet("M")
                R.id.key_n -> Key.Alphabet("N")
                R.id.key_o -> Key.Alphabet("O")
                R.id.key_p -> Key.Alphabet("P")
                R.id.key_q -> Key.Alphabet("Q")
                R.id.key_r -> Key.Alphabet("R")
                R.id.key_s -> Key.Alphabet("S")
                R.id.key_t -> Key.Alphabet("T")
                R.id.key_u -> Key.Alphabet("U")
                R.id.key_v -> Key.Alphabet("V")
                R.id.key_w -> Key.Alphabet("W")
                R.id.key_x -> Key.Alphabet("X")
                R.id.key_y -> Key.Alphabet("Y")
                R.id.key_z -> Key.Alphabet("Z")
                R.id.key_return -> Key.Return
                R.id.key_backspace -> Key.Backspace
                else -> throw IllegalArgumentException("it.id :${it.id}")
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

    fun setOnKeyListener(onKeyListener: OnKeyListener) {
        this.onKeyListener = onKeyListener
    }

    sealed class Key {
        data class Alphabet(val letter: String) : Key()
        object Return : Key()
        object Backspace : Key()
    }
}