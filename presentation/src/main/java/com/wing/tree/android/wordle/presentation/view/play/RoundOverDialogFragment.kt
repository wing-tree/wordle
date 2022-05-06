package com.wing.tree.android.wordle.presentation.view.play

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wing.tree.android.wordle.presentation.databinding.FragmentRoundOverDialogBinding
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible
import com.wing.tree.android.wordle.presentation.view.base.BaseDialogFragment

class RoundOverDialogFragment : BaseDialogFragment<FragmentRoundOverDialogBinding>() {
    interface OnClickListener {
        fun onOneMoreTryClick()
        fun onNoThanksClick()
        fun onPlayAgainClick()
    }

    private val roundAdded by lazy { arguments?.getBoolean(Key.ROUND_ADDED) ?: false }

    private var onClickListener: OnClickListener? = null

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRoundOverDialogBinding {
        return FragmentRoundOverDialogBinding.inflate(inflater, container, false)
    }

    override fun initData() {

    }

    override fun bind(viewBinding: FragmentRoundOverDialogBinding) {
        with(viewBinding) {
            if (roundAdded) {
                materialButtonAddRound.gone()
            } else {
                materialButtonAddRound.visible()

                materialButtonAddRound.setOnClickListener {
                    onClickListener?.onOneMoreTryClick()
                    dismiss()
                }
            }

            materialButtonTryAgain.setOnClickListener {
                onClickListener?.onPlayAgainClick()
                dismiss()
            }

            materialButtonNoThanks.setOnClickListener {
                onClickListener?.onNoThanksClick()
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnClickListener) {
            onClickListener = context
        } else {
            parentFragment?.let {
                if (it is OnClickListener) {
                    onClickListener = it
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    companion object {
        private object Key {
            private const val OBJECT_NAME = "Key"

            const val ROUND_ADDED = "$OBJECT_NAME.ROUND_ADDED"
        }

        fun newInstance(roundAdded: Boolean): RoundOverDialogFragment {
            return RoundOverDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(Key.ROUND_ADDED, roundAdded)
                }
            }
        }
    }
}