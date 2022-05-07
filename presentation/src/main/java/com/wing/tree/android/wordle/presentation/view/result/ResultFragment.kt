package com.wing.tree.android.wordle.presentation.view.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wing.tree.android.wordle.presentation.databinding.FragmentResultBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.PlayResult
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.result.ResultViewModel
import com.wing.tree.android.wordle.presentation.widget.LetterView
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>() {
    private val viewModel by viewModels<ResultViewModel>()
    private val navArgs: ResultFragmentArgs by navArgs()
    private val featureFlag = LetterView.FeatureFlag.Result

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentResultBinding {
        return FragmentResultBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.statistics.observe(viewLifecycleOwner) {
            viewBinding.statisticsView.setStatistics(it)
        }
    }

    override fun bind(viewBinding: FragmentResultBinding) {
        with(viewBinding) {
            materialButtonNextWord.setOnClickListener {
                findNavController().navigate(ResultFragmentDirections.actionResultFragmentToPlayFragment())
            }

            when(val playResult = navArgs.playResult) {
                is PlayResult.Lose -> {
                    val letters = playResult.letters
                    val states = playResult.states

                    repeat(WORD_LENGTH) {
                        val state = Letter.State.fromInt(states[it])
                        val letter = Letter(it, letters[it]).apply { updateState(state) }

                        word[it].submitLetter(letter, featureFlag)
                    }
                }
                is PlayResult.Win -> {
                    playResult.word.forEachIndexed { index, letter ->
                        with(Letter(index, letter)) {
                            submit()
                            word[index].submitLetter(this, featureFlag)
                        }
                    }
                }
            }
        }
    }

    private fun getColor(@ColorRes id: Int) = requireContext().getColor(id)
}