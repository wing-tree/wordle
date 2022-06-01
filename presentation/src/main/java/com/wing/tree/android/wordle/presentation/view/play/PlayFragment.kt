package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.adapter.play.ItemDecoration
import com.wing.tree.android.wordle.presentation.adapter.play.PlayBoardListAdapter
import com.wing.tree.android.wordle.presentation.constant.Duration
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegate
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegateImpl
import com.wing.tree.android.wordle.presentation.extention.*
import com.wing.tree.android.wordle.presentation.model.play.Key
import com.wing.tree.android.wordle.presentation.model.play.ViewState
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import com.wing.tree.wordle.core.exception.HardModeConditionNotMetException
import com.wing.tree.wordle.core.exception.WordNotFoundException
import com.wing.tree.wordle.core.util.half
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

@DelicateCoroutinesApi
@AndroidEntryPoint
class PlayFragment: BaseFragment<FragmentPlayBinding>(),
    RoundOverDialogFragment.OnClickListener, InterstitialAdDelegate by InterstitialAdDelegateImpl()
{
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private val viewModel by viewModels<PlayViewModel>()
    private val playBoardListAdapter = PlayBoardListAdapter(
        object : PlayBoardListAdapter.Callbacks {
            override fun onLetterClick(adapterPosition: Int, index: Int) {
                viewModel.removeAt(adapterPosition, index)
            }

            override fun onAnimationEnd() {
                if (viewModel.round > MAXIMUM_ROUND.half) {
                    viewBinding.recyclerView.smoothSnapToPosition(viewModel.round)
                }

                viewModel.requestFocus()
                viewModel.setAnimating(false)
            }

            override fun beforeAnimationStart() {
                viewModel.setAnimating(true)
            }
        }
    )

    private val currentItemView: View?
        get() = viewBinding.recyclerView.findViewHolderForAdapterPosition(viewModel.round)?.itemView

    private val isAdsRemoved = AtomicBoolean(false)

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
                when(key) {
                    is Key.Alphabet -> viewModel.add(key.letter)
                    is Key.Return -> buttonSubmit.callOnClick()
                    is Key.Backspace -> imageViewBackspace.callOnClick()
                }
            }

            itemFloatingActionButtonEraser.credits = Item.Eraser.credits
            itemFloatingActionButtonHint.credits = Item.Hint.credits

            itemFloatingActionButtonEraser.setOnClickListener {
                viewModel.consumeItem(Item.Eraser)
            }

            itemFloatingActionButtonHint.setOnClickListener {
                viewModel.consumeItem(Item.Hint)
            }

            buttonSubmit.setOnClickListener {
                viewModel.submit {
                    it.onFailure { throwable ->
                        when(throwable) {
                            is HardModeConditionNotMetException.Matched -> {
                                val prefix = when(throwable.position) {
                                    0 -> getString(R.string.first)
                                    1 -> getString(R.string.second)
                                    2 -> getString(R.string.third)
                                    3 -> getString(R.string.fourth)
                                    4 -> getString(R.string.fifth)
                                    else -> BLANK
                                }

                                val letter = throwable.letter.first().titlecase()
                                val text = "$prefix ${getString(R.string.hard_mode_000)} $letter"

                                showMaterialCardViewToast(text)
                            }
                            is HardModeConditionNotMetException.Mismatched -> {
                                val letter = throwable.letter.first().titlecase()
                                val text = "${getString(R.string.hard_mode_001)} $letter"

                                showMaterialCardViewToast(text)
                            }
                            is WordNotFoundException -> currentItemView?.shake()
                        }

                    }
                }
            }

            imageViewBackspace.setOnClickListener {
                if (it.isPressed) {
                    it.scaleUpDown()
                }

                viewModel.removeLast()
            }
        }
    }

    override fun initData() {
        loadInterstitialAd(requireContext())
        viewModel.setRunsAnimation(false)

        activityViewModel.isAdsRemoved.observe(viewLifecycleOwner) {
            isAdsRemoved.set(it)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.itemCount.collectLatest {
                viewBinding.itemFloatingActionButtonEraser.count = it.eraser
                viewBinding.itemFloatingActionButtonHint.count = it.hint
            }
        }

        viewModel.isHighContrastMode.observe(viewLifecycleOwner) {
            playBoardListAdapter.isHighContrastMode = it
        }

        viewModel.playBoard.observe(viewLifecycleOwner) {
            if (it.currentLine.isFilled) {
                viewBinding.keyboardView.enableReturnKey()
            } else {
                viewBinding.keyboardView.disableReturnKey()
            }

            playBoardListAdapter.submitPlayBoard(it) {
                it.runsAnimation.set(true)

                if (it.isRoundAdded) {
                    lifecycleScope.launch {
                        delay(Duration.SHORT)
                        viewBinding.recyclerView.smoothSnapToPosition(it.round)
                    }
                }
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
            viewBinding.keyboardView.submitKeyboard(it) {
                it.runsAnimation.set(true)
            }
        }

        viewModel.vibrates.observe(viewLifecycleOwner) {
            viewBinding.keyboardView.setVibrates(it)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            when(viewState) {
                is ViewState.Error -> {
                    val message = viewState.throwable.message
                    val cause = viewState.throwable.cause
                    val text = """
                        message: $message
                        cause :$cause
                    """.trimIndent()

                    showToast(text)
                }
                is ViewState.Loading -> { viewModel.disableKeyboard() }
                is ViewState.Play -> { viewModel.enableKeyboard() }
                is ViewState.Ready -> {
                    viewModel.disableKeyboard()
                }
                is ViewState.RoundOver -> {
                    RoundOverDialogFragment.newInstance(viewState.isRoundAdded).also {
                        it.show(childFragmentManager, it.tag)
                    }
                }
                is ViewState.Finish -> {
                    val directions = PlayFragmentDirections.actionPlayFragmentToResultFragment(viewState.playResult)

                    when(viewState) {
                        is ViewState.Finish.Lose -> navigate(directions)
                        is ViewState.Finish.Win -> navigate(directions)
                    }
                }
            }
        }
    }

    override fun onNoThanksClick(dialogFragment: DialogFragment) {
        viewModel.lose()
        dialogFragment.dismiss()
    }

    override fun onOneMoreTryClick(dialogFragment: DialogFragment) {
        viewModel.consumeItem(Item.OneMoreTry)
        dialogFragment.dismiss()
    }

    override fun onPlayAgainClick(dialogFragment: DialogFragment) {
        lifecycleScope.launch(mainDispatcher) {
            dialogFragment.dismiss()
            delay(Duration.SHORT)

            if (isAdsRemoved.get()) {
                playAgain()
            } else {
                showInterstitialAd(
                    requireActivity(),
                    onAdFailedToShowFullScreenContent = {
                        Timber.e("$it")
                        playAgain()
                    },
                    onAdShowedFullScreenContent = {
                        playAgain()
                    }
                )
            }
        }
    }

    private fun playAgain() = with(viewBinding.recyclerView) {
        fadeOut {
            removeAllViews()
            viewModel.playAgain()
            fadeIn()
        }
    }

    private fun showMaterialCardViewToast(text: String) {
        with(viewBinding) {
            textViewToast.text = text
            materialCardViewToast.fadeIn(Duration.LONG) {
                lifecycleScope.launch {
                    delay(Duration.LONG)
                    materialCardViewToast.fadeOut(Duration.MEDIUM)
                }
            }
        }
    }
}