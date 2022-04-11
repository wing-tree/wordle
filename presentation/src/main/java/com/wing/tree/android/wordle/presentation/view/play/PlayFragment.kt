package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.adapter.play.AdapterItem
import com.wing.tree.android.wordle.presentation.adapter.play.LettersListAdapter
import com.wing.tree.android.wordle.presentation.constant.Try
import com.wing.tree.android.wordle.presentation.constant.Word
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.model.play.Result
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import com.wing.tree.android.wordle.presentation.widget.KeyboardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFragment: BaseFragment<FragmentPlayBinding>() {
    private val viewModel by viewModels<PlayViewModel>()
    private val lettersListAdapter = LettersListAdapter(
        object : LettersListAdapter.Callbacks {
            override fun onLetterClick(adapterPosition: Int, index: Int) {
                viewModel.removeAt(adapterPosition, index)
            }

            override fun onFlipped(letters: AdapterItem.Letters) {
                viewModel.enableKeyboard()
            }
        }
    )

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayBinding {
        return FragmentPlayBinding.inflate(inflater, container, false)
    }

    override fun bind(viewBinding: FragmentPlayBinding) {
        with(viewBinding) {
            recyclerView.apply {
                recycledViewPool.setMaxRecycledViews(0, 0)

                adapter = lettersListAdapter
                itemAnimator = null
                layoutManager = LinearLayoutManager(context).apply {
                    initialPrefetchItemCount = Try.MAXIMUM
                }
            }

            keyboard.setOnKeyListener { key ->
                when(key) {
                    is KeyboardView.Key.Alphabet -> viewModel.add(key.letter)
                    is KeyboardView.Key.Return -> {
                        viewModel.currentLetters?.let {
                            if (it.length == Word.LENGTH) {
                                viewModel.disableKeyboard()
                                viewModel.submit(it)
                            }
                        }
                    }
                    is KeyboardView.Key.Backspace -> viewModel.removeLast()
                }
            }
        }
    }

    override fun initData() {
        viewModel.load {
            viewModel.enableKeyboard()
        }

        viewModel.keyboardEnabled.observe(viewLifecycleOwner) { enabled ->
            if (enabled) {
                viewBinding.keyboard.enable()
            } else {
                viewBinding.keyboard.disable()
            }
        }

        viewModel.letters.observe(viewLifecycleOwner) {
            lettersListAdapter.submitList(
                it.mapIndexed { index, letters ->
                    AdapterItem.Letters.from(index, letters)
                }
            )
        }

        viewModel.result.observe(viewLifecycleOwner) {
            when(it) {
                Result.Lose -> {}
                Result.Win -> {}
            }
        }
    }
}