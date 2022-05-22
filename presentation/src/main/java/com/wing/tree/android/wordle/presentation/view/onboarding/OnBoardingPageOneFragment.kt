package com.wing.tree.android.wordle.presentation.view.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import com.wing.tree.android.wordle.presentation.databinding.FragmentOnBoardingPageOneBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.Line
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.widget.LetterView
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import com.wing.tree.wordle.core.util.isZero

class OnBoardingPageOneFragment: BaseFragment<FragmentOnBoardingPageOneBinding>() {
    private val line1 = Line(0).apply {
        val word = "weary"

        repeat(WORD_LENGTH) {
            val letter = Letter(it, word[it]).apply {
                if (it.isZero) {
                    submit()
                }
            }

            set(it, letter)
        }
    }

    private val line2 = Line(0).apply {
        val word = "pills"

        repeat(WORD_LENGTH) {
            val letter = Letter(it, word[it]).apply {
                if (it == 1) {
                    updateState(Letter.State.In.Mismatched())
                }
            }

            set(it, letter)
        }
    }

    private val line3 = Line(0).apply {
        val word = "vague"

        repeat(WORD_LENGTH) {
            val letter = Letter(it, word[it]).apply {
                if (it == 3) {
                    updateState(Letter.State.NotIn())
                }
            }

            set(it, letter)
        }
    }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoardingPageOneBinding {
        return FragmentOnBoardingPageOneBinding.inflate(inflater, container, false)
    }

    override fun initData() {

    }

    override fun bind(viewBinding: FragmentOnBoardingPageOneBinding) {
        with(viewBinding) {
            val flag = LetterView.Flag.Default(LetterView.Flag.Action.Nothing)

            lineView1.submitLetters(line1.currentLetters, flag)
            lineView2.submitLetters(line2.currentLetters, flag)
            lineView3.submitLetters(line3.currentLetters, flag)
        }
    }
}