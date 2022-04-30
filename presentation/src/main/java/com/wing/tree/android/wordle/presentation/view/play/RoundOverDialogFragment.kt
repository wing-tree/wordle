package com.wing.tree.android.wordle.presentation.view.play

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wing.tree.android.wordle.presentation.databinding.FragmentRoundOverDialogBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseDialogFragment

class RoundOverDialogFragment : BaseDialogFragment<FragmentRoundOverDialogBinding>() {
    interface OnClickListener {
        fun onAddRoundClick()
        fun onNoThanksClick()
        fun onTryAgainClick()
    }

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
            materialButtonAddRound.setOnClickListener {
                onClickListener?.onAddRoundClick()
                dismiss()
            }

            materialButtonTryAgain.setOnClickListener {
                onClickListener?.onTryAgainClick()
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
        fun newInstance(): RoundOverDialogFragment {
            return RoundOverDialogFragment().apply {

            }
        }
    }
}