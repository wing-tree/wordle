package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.adapter.play.BoardListAdapter
import com.wing.tree.android.wordle.presentation.adapter.play.ItemDecoration
import com.wing.tree.android.wordle.presentation.constant.Round
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegate
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegateImpl
import com.wing.tree.android.wordle.presentation.extention.scale
import com.wing.tree.android.wordle.presentation.model.play.Key
import com.wing.tree.android.wordle.presentation.model.play.State
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PlayFragment: BaseFragment<FragmentPlayBinding>(),
    RoundOverDialogFragment.OnClickListener, InterstitialAdDelegate by InterstitialAdDelegateImpl()
{
    private val viewModel by viewModels<PlayViewModel>()
    private val boardListAdapter = BoardListAdapter(
        object : BoardListAdapter.Callbacks {
            override fun onLetterClick(adapterPosition: Int, index: Int) {
                viewModel.removeAt(adapterPosition, index)
            }

            override fun onAnimationEnd() {
                viewModel.isAnimating.postValue(false)
            }

            override fun onAnimationStart() {
                viewModel.isAnimating.postValue(true)
            }
        }
    )

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayBinding {
        return FragmentPlayBinding.inflate(inflater, container, false)
    }

    override fun bind(viewBinding: FragmentPlayBinding) {
        with(viewBinding) {
            recyclerView.apply {
                addItemDecoration(ItemDecoration())
                recycledViewPool.setMaxRecycledViews(0, 0)

                with(itemAnimator) {
                    if (this is DefaultItemAnimator) {
                        supportsChangeAnimations = false
                    }
                }

                adapter = boardListAdapter
                layoutManager = LinearLayoutManager(context).apply {
                    initialPrefetchItemCount = Round.MAXIMUM
                }
            }

            keyboardView.setOnKeyListener { key ->
                viewModel.playSound()

                when(key) {
                    is Key.Alphabet -> viewModel.add(key.letter)
                    // todo sjk check 아래의 접근으로 shake 적용 가능.
                    is Key.Return -> { viewModel.submit { recyclerView.findViewHolderForAdapterPosition(1)?.itemView?.scale(1.0F, 1.5F, 500L) } }
                    is Key.Backspace -> viewModel.removeLast()
                }
            }

            textViewHint.setOnClickListener {
                viewModel.useHint()
            }

            textViewDart.setOnClickListener {
                viewModel.useEraser()
            }
        }
    }

    override fun initData() {
        loadInterstitialAd(requireContext())

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when(state) {
                is State.Error -> {}
                is State.Play -> {}
                is State.Ready -> {}
                is State.Finish -> {
                    when(state) {
                        is State.Finish.RoundOver -> {
                            RoundOverDialogFragment.newInstance(state.isRoundAdded).also {
                                it.show(childFragmentManager, it.tag)
                            }
                        }
                        is State.Finish.Win -> {}
                    }
                }
            }
        }

        viewModel.playBoard.observe(viewLifecycleOwner) {
            boardListAdapter.submitBoard(it)
        }

        viewModel.load {
            viewModel.enableKeyboard()
        }

        viewModel.keyboardEnabled.observe(viewLifecycleOwner) { enabled ->
            if (enabled) {
                viewBinding.keyboardView.enable()
            } else {
                viewBinding.keyboardView.disable()
            }
        }

        viewModel.keyboard.observe(viewLifecycleOwner) {
            viewBinding.keyboardView.applyState(it.alphabets)
        }

        viewModel.directions.observe(viewLifecycleOwner) {
            navigate(it)
        }
    }

    override fun onAddRoundClick() {
        viewModel.addRound()
    }

    override fun onNoThanksClick() {
        navigate(PlayFragmentDirections.actionPlayFragmentToResultFragment())
    }

    override fun onTryAgainClick() {
        showInterstitialAd(
            requireActivity(),
            onAdDismissedFullScreenContent = {
                viewModel.tryAgain()
            },
            onAdFailedToShowFullScreenContent = {
                Timber.e("$it")
                viewModel.tryAgain()
            }
        )
    }

    private fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}