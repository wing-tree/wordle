package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wing.tree.android.wordle.presentation.adapter.play.AdapterItem
import com.wing.tree.android.wordle.presentation.adapter.play.ItemDecoration
import com.wing.tree.android.wordle.presentation.adapter.play.LettersListAdapter
import com.wing.tree.android.wordle.presentation.constant.Attempt
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
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

            override fun onAnimationEnd() {
                viewModel.flipIsRunning.postValue(false)
            }

            override fun onAnimationStart() {
                viewModel.flipIsRunning.postValue(true)
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

                adapter = lettersListAdapter
                layoutManager = LinearLayoutManager(context).apply {
                    initialPrefetchItemCount = Attempt.MAXIMUM
                }
            }

            keyboardView.setOnKeyListener { key ->
                when(key) {
                    is KeyboardView.Key.Alphabet -> viewModel.add(key.letter)
                    is KeyboardView.Key.Return -> { viewModel.submit {  } }
                    is KeyboardView.Key.Backspace -> viewModel.removeLast()
                }
            }

            textViewHint.setOnClickListener {
                viewModel.useHint()
            }

            textViewDart.setOnClickListener {
                viewModel.useDart()
            }
        }
    }

    override fun initData() {
        viewModel.board.observe(viewLifecycleOwner) {
            lettersListAdapter.submitBoard(it)
        }

        viewModel.showAddAttemptDialog.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle("기회 추가")
                .setPositiveButton("구매") { _, _ ->
                    viewModel.addAttempt()
                }
                .setNegativeButton("꺼져") { _, _ ->
                    viewModel.lose()
                }
                .show()
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

        viewModel.keys.observe(viewLifecycleOwner) {
            viewBinding.keyboardView.applyState(it)
        }

        viewModel.directions.observe(viewLifecycleOwner) {
            navigate(it)
        }
    }

    private fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}