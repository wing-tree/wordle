package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.domain.model.item.ItemCount
import com.wing.tree.android.wordle.presentation.adapter.play.ItemDecoration
import com.wing.tree.android.wordle.presentation.adapter.play.PlayBoardListAdapter
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegate
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegateImpl
import com.wing.tree.android.wordle.presentation.extention.shake
import com.wing.tree.android.wordle.presentation.model.play.Key
import com.wing.tree.android.wordle.presentation.model.play.PlayResult
import com.wing.tree.android.wordle.presentation.model.play.ViewState
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@DelicateCoroutinesApi
@AndroidEntryPoint
class PlayFragment: BaseFragment<FragmentPlayBinding>(),
    RoundOverDialogFragment.OnClickListener, InterstitialAdDelegate by InterstitialAdDelegateImpl()
{
    private val viewModel by viewModels<PlayViewModel>()
    private val playBoardListAdapter = PlayBoardListAdapter(
        object : PlayBoardListAdapter.Callbacks {
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

    private val currentItemView: View?
        get() = viewBinding.recyclerView
            .findViewHolderForAdapterPosition(viewModel.round)?.itemView

    override fun onPause() {
        viewModel.updatePlayState()
        super.onPause()
    }

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

                adapter = playBoardListAdapter
                layoutManager = LinearLayoutManager(context).apply {
                    initialPrefetchItemCount = MAXIMUM_ROUND
                }
            }

            keyboardView.setOnKeyListener { key ->
                viewModel.playSound()

                when(key) {
                    is Key.Alphabet -> viewModel.add(key.letter)
                    is Key.Return -> {
                        viewModel.submit { it.onFailure { currentItemView?.shake() } }
                    }
                    is Key.Backspace -> viewModel.removeLast()
                }
            }

            textViewHint.setOnClickListener {
                viewModel.useItem(Item.Type.HINT)
            }

            textViewEraser.setOnClickListener {
                viewModel.useItem(Item.Type.ERASER)
            }
        }
    }

    override fun initData() {
        loadInterstitialAd(requireContext())

        lifecycleScope.launch {
            viewModel.itemCount.collect {
                viewBinding.textViewEraserCount.text = "${it.eraser}"
                viewBinding.textViewHintCount.text = "${it.hint}"
            }
        }

        viewModel.playBoard.observe(viewLifecycleOwner) {
            playBoardListAdapter.submitPlayBoard(it) {
                it.skipAnimation.set(false)
            }
        }

        viewModel.keyboardEnabled.observe(viewLifecycleOwner) { enabled ->
            if (enabled) {
                viewBinding.keyboardView.enable()
            } else {
                viewBinding.keyboardView.disable()
            }
        }

        viewModel.keyboard.observe(viewLifecycleOwner) {
            viewBinding.keyboardView.applyState(it.alphabetKeys)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Error -> {}
                is ViewState.Loading -> { viewModel.disableKeyboard() }
                is ViewState.Play -> { viewModel.enableKeyboard() }
                is ViewState.Ready -> { viewModel.disableKeyboard() }
                is ViewState.Finish -> {
                    when(state) {
                        is ViewState.Finish.RoundOver -> {
                            RoundOverDialogFragment.newInstance(state.isRoundAdded).also {
                                it.show(childFragmentManager, it.tag)
                            }
                        }
                        is ViewState.Finish.Win -> {
                            val round = viewModel.round
                            val word = viewModel.word.value

                            val win =  PlayResult.Win(
                                round = round,
                                word = word
                            )

                            val directions = PlayFragmentDirections.actionPlayFragmentToResultFragment(win)

                            navigate(directions)
                        }
                    }
                }
            }
        }
    }

    override fun onNoThanksClick() {
        with(viewModel) {
            playBoard.value?.let { playBoard ->
                val closest = playBoard.closest
                val letters = closest.string
                val round = round
                val states = closest.map { it.state.toInt() }
                val word = word.value

                val lose = PlayResult.Lose(
                    letters = letters,
                    round = round,
                    states = states,
                    word = word
                )

                val directions = PlayFragmentDirections.actionPlayFragmentToResultFragment(lose)

                navigate(directions)
            }
        }
    }

    override fun onOneMoreTryClick() {
        viewModel.useItem(Item.Type.ONE_MORE_TRY)
    }

    override fun onPlayAgainClick() {
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
}