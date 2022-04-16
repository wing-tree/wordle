package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.presentation.adapter.play.AdapterItem
import com.wing.tree.android.wordle.presentation.adapter.play.LettersListAdapter
import com.wing.tree.android.wordle.presentation.constant.Try
import com.wing.tree.android.wordle.presentation.constant.Word
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import com.wing.tree.android.wordle.presentation.widget.KeyboardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PlayFragment: BaseFragment<FragmentPlayBinding>() {
    private val viewModel by viewModels<PlayViewModel>()
    private val lettersListAdapter = LettersListAdapter(
        object : LettersListAdapter.Callbacks {
            override fun onLetterClick(adapterPosition: Int, index: Int) {
                viewModel.removeAt(adapterPosition, index)
            }

            override fun onFlipped(letters: AdapterItem.Letters) {
                checkResult()
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

            keyboardView.setOnKeyListener { key ->
                when(key) {
                    is KeyboardView.Key.Alphabet -> viewModel.add(key.letter)
                    is KeyboardView.Key.Return -> {
                        viewModel.currentLetters?.let {
                            if (it.length == Word.LENGTH) {
                                viewModel.submit(it) { letters ->

                                }
                            }
                        }
                    }
                    is KeyboardView.Key.Backspace -> viewModel.removeLast()
                }
            }

            textViewHint.setOnClickListener {
                viewModel.useHint()
            }
        }
    }

    override fun initData() {
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

        viewModel.letters.observe(viewLifecycleOwner) {
            lettersListAdapter.submitList(
                it.mapIndexed { index, letters ->
                    AdapterItem.Letters.from(index, letters)
                }
            )
        }

        viewModel.keys.observe(viewLifecycleOwner) {
            viewBinding.keyboardView.applyState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        checkResult()
    }

    private fun checkResult() {
        val directions = PlayFragmentDirections.actionPlayFragmentToResultFragment()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(250)

            withContext(Dispatchers.Main.immediate) {
                when (viewModel.checkResult()) {
                    Result.Lose -> navigate(directions)
                    Result.Win -> navigate(directions)
                }
            }
        }
    }

    private fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}