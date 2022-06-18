package com.wing.tree.android.wordle.presentation.view.play

import android.os.VibrationEffect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.wing.tree.android.wordle.presentation.util.Vibrator
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import com.wing.tree.wordle.core.exception.HardModeConditionNotMetException
import com.wing.tree.wordle.core.exception.NotEnoughLettersException
import com.wing.tree.wordle.core.exception.WordNotFoundException
import com.wing.tree.wordle.core.util.half
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

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
    private val ordinalNumbers by lazy { resources.getStringArray(R.array.ordinal_numbers) }
    private val toastIsShowing = AtomicBoolean(false)
    private val vibrates = AtomicBoolean(true)

    @Inject
    lateinit var vibrator: Vibrator

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
                if (vibrates.get()) {
                    vibrator.vibrate()
                }

                it.scaleUpDown()

                viewModel.consumeItem(Item.Eraser)
            }

            itemFloatingActionButtonHint.setOnClickListener {
                if (vibrates.get()) {
                    vibrator.vibrate()
                }

                it.scaleUpDown()

                viewModel.consumeItem(Item.Hint)
            }

            buttonSubmit.setOnClickListener { view ->
                if (view.isPressed) {
                    if (vibrates.get()) {
                        vibrator.vibrate()
                    }

                    view.scaleUpDown()
                }

                viewModel.submit {
                    it.onFailure { throwable ->
                        when(throwable) {
                            is HardModeConditionNotMetException.Matched -> {
                                val prefix = try {
                                    ordinalNumbers[throwable.position]
                                } catch (arrayIndexOutOfBoundsException: ArrayIndexOutOfBoundsException) {
                                    Timber.e(arrayIndexOutOfBoundsException)
                                    BLANK
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
                            is NotEnoughLettersException -> showMaterialCardViewToast(getString(R.string.not_enough_letters))
                            is WordNotFoundException -> showMaterialCardViewToast(getString(R.string.word_not_found))
                        }

                        if (vibrates.get()) {
                            vibrator.vibrate(40L, VibrationEffect.DEFAULT_AMPLITUDE)
                        }

                        currentItemView?.shake()
                    }
                }
            }

            imageViewBackspace.setOnClickListener {
                if (it.isPressed) {
                    if (vibrates.get()) {
                        vibrator.vibrate()
                    }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.itemCount.collectLatest {
                    viewBinding.itemFloatingActionButtonEraser.count = it.eraser
                    viewBinding.itemFloatingActionButtonHint.count = it.hint
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                activityViewModel.settings.collectLatest { settings ->
                    vibrates.set(settings.vibrates)

                    playBoardListAdapter.isHighContrastMode = settings.isHighContrastMode
                    viewBinding.keyboardView.setVibrates(settings.vibrates)
                    viewModel.setHardMode(settings.isHardMode)
                }
            }
        }

        viewModel.playBoard.observe(viewLifecycleOwner) {
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
                is ViewState.Loading -> viewModel.disableKeyboard()
                is ViewState.Play -> viewModel.enableKeyboard()
                is ViewState.Ready -> viewModel.disableKeyboard()
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
        if (toastIsShowing.get()) {
            return
        }

        with(viewBinding) {
            textViewToast.text = text
            toastIsShowing.set(true)

            materialCardViewToast.fadeIn(Duration.LONG) {
                lifecycleScope.launch {
                    delay(Duration.LONG)
                    materialCardViewToast.fadeOut(Duration.MEDIUM) {
                        toastIsShowing.set(false)
                    }
                }
            }
        }
    }
}