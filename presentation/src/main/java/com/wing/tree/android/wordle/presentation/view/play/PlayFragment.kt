package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.adapter.play.AdapterItem
import com.wing.tree.android.wordle.presentation.adapter.play.LettersListAdapter
import com.wing.tree.android.wordle.presentation.constant.Try
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.play.PlayViewModel
import com.wing.tree.android.wordle.presentation.widget.Keyboard

class PlayFragment: BaseFragment<FragmentPlayBinding>() {
    private val viewModel by viewModels<PlayViewModel>()
    private val lettersListAdapter = LettersListAdapter()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayBinding {
        return FragmentPlayBinding.inflate(inflater, container, false)
    }

    override fun bind(viewBinding: FragmentPlayBinding) {
        with(viewBinding) {
            recyclerView.apply {
                adapter = lettersListAdapter
                layoutManager = LinearLayoutManager(context).apply {
                    initialPrefetchItemCount = Try.Maximum
                }
            }

            keyboard.setOnKeyListener { key ->
                when(key) {
                    is Keyboard.Key.Alphabet -> viewModel.addLetter(key.letter)
                    is Keyboard.Key.Return -> {}
                    is Keyboard.Key.Backspace -> viewModel.removeLastLetter()
                }
            }
        }
    }

    override fun initData() {
        viewModel.letters.observe(viewLifecycleOwner) {
            lettersListAdapter.submitList(
                it.mapIndexed { index, letters ->
                    AdapterItem.Letters.from(index, letters)
                }
            )
        }
    }

    companion object {
        fun newInstance(): PlayFragment {
            return PlayFragment()
        }
    }
}