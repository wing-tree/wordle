package com.wing.tree.android.wordle.presentation.view.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.wing.tree.android.wordle.presentation.databinding.FragmentResultBinding
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegate
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegateImpl
import com.wing.tree.android.wordle.presentation.extention.visible
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.PlayResult
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.result.ResultViewModel
import com.wing.tree.android.wordle.presentation.widget.LetterView
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import com.wing.tree.wordle.core.constant.isZero
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

@DelicateCoroutinesApi
@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>(), InterstitialAdDelegate by InterstitialAdDelegateImpl() {
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private val viewModel by viewModels<ResultViewModel>()

    private val flag = LetterView.Flag.Result
    private val isAdsRemoved = AtomicBoolean(false)
    private val navArgs: ResultFragmentArgs by navArgs()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentResultBinding {
        return FragmentResultBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        activityViewModel.isAdsRemoved.observe(viewLifecycleOwner) {
            isAdsRemoved.set(it)
        }

        viewModel.statistics.observe(viewLifecycleOwner) {
            viewBinding.statisticsView.setStatistics(it)
        }
    }

    override fun bind(viewBinding: FragmentResultBinding) {
        with(viewBinding) {
            val playResult = navArgs.playResult

            when(playResult) {
                is PlayResult.Lose -> {
                    val letters = playResult.letters
                    val states = playResult.states

                    repeat(WORD_LENGTH) {
                        val state = Letter.State.fromInt(states[it])
                        val letter = Letter(it, letters[it]).apply { updateState(state) }

                        word[it].submitLetter(letter, flag)
                    }
                }
                is PlayResult.Win -> {
                    playResult.word.forEachIndexed { index, letter ->
                        with(Letter(index, letter)) {
                            submit()
                            word[index].submitLetter(this, flag)
                        }
                    }
                }
            }

            materialButtonNextWord.setOnClickListener {
                val directions = ResultFragmentDirections.actionResultFragmentToPlayFragment()

                if (activityViewModel.played.getAndIncrement().mod(INTERSTITIAL_AD_CYCLE).isZero) {
                    if (isAdsRemoved.get()) {
                        navigate(directions)
                    } else {
                        loadInterstitialAd(
                            requireContext(),
                            onAdFailedToLoad = {
                                Timber.e("$it")
                                navigate(directions)
                            },
                            onAdLoaded = {
                                showInterstitialAd(
                                    activity = requireActivity(),
                                    interstitialAd = it,
                                    onAdDismissedFullScreenContent = { },
                                    onAdFailedToShowFullScreenContent = { navigate(directions) },
                                    onAdShowedFullScreenContent = { navigate(directions) },
                                )
                            }
                        )
                    }
                } else {
                    navigate(directions)
                }
            }

            if (playResult is PlayResult.Lose) {
                textViewAnswer.visible()
                textViewAnswer.setOnClickListener {
                    if (isAdsRemoved.get()) {
                        showAnswer(playResult)
                    } else {
                        loadInterstitialAd(
                            requireContext(),
                            onAdFailedToLoad = {
                                Timber.e("$it")
                                showAnswer(playResult)
                            },
                            onAdLoaded = {
                                showInterstitialAd(
                                    activity = requireActivity(),
                                    interstitialAd = it,
                                    onAdDismissedFullScreenContent = { showAnswer(playResult) },
                                    onAdFailedToShowFullScreenContent = { showAnswer(playResult) },
                                    onAdShowedFullScreenContent = { },
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun showAnswer(playResult: PlayResult.Lose) {
        playResult.word.forEachIndexed { index, letter ->
            with(Letter(index, letter)) {
                submit()
                viewBinding.word[index].submitLetter(this, LetterView.Flag.Submit)
            }

            viewBinding.word.flipAll()
        }
    }

    companion object {
        const val INTERSTITIAL_AD_CYCLE = 3
    }
}