package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.presentation.adapter.play.ItemDecoration
import com.wing.tree.android.wordle.presentation.adapter.play.PlayBoardListAdapter
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegate
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegateImpl
import com.wing.tree.android.wordle.presentation.extention.shake
import com.wing.tree.android.wordle.presentation.model.play.Key
import com.wing.tree.android.wordle.presentation.model.play.ViewState
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
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
                    is Key.Backspace -> viewModel.removeLast()
                }
            }

            textViewEraser.setOnClickListener {
                viewModel.consumeItem(Item.Type.ERASER)
            }

            textViewHint.setOnClickListener {
                viewModel.consumeItem(Item.Type.HINT)
            }

            buttonSubmit.setOnClickListener {
                viewModel.submit { it.onFailure { currentItemView?.shake() } }
            }
        }
    }

    override fun initData() {
        loadInterstitialAd(requireContext())
        viewModel.setRunsAnimation(false)

        activityViewModel.isAdsRemoved.observe(viewLifecycleOwner) {
            isAdsRemoved.set(it)
        }

        lifecycleScope.launch {
            viewModel.itemCount.collect {
                viewBinding.textViewEraserCount.text = "${it.eraser}"
                viewBinding.textViewHintCount.text = "${it.hint}"
            }
        }

        viewModel.playBoard.observe(viewLifecycleOwner) {
            playBoardListAdapter.submitPlayBoard(it) {
                it.runsAnimation.set(true)
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
                is ViewState.Error -> {}
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

    override fun onNoThanksClick() {
        viewModel.lose()
    }

    override fun onOneMoreTryClick() {
        viewModel.consumeItem(Item.Type.ONE_MORE_TRY)
    }

    override fun onPlayAgainClick() {
        if (isAdsRemoved.get()) {
            viewModel.tryAgain()
        } else {
            showInterstitialAd(
                requireActivity(),
                onAdFailedToShowFullScreenContent = {
                    Timber.e("$it")
                    viewModel.tryAgain()
                },
                onAdShowedFullScreenContent = {
                    viewModel.tryAgain()
                }
            )
        }
    }
}