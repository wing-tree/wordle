package com.wing.tree.android.wordle.presentation.adapter.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.constant.Word
import com.wing.tree.android.wordle.presentation.databinding.LettersItemBinding
import com.wing.tree.android.wordle.presentation.extention.scale
import java.lang.NullPointerException
import com.wing.tree.android.wordle.presentation.model.Letters as PresentationModel

class LettersListAdapter : ListAdapter<AdapterItem, LettersListAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val viewBinding: LettersItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.Letters -> with(viewBinding) {
                    with(item) {
                        letters.forEachIndexed { index, letter ->
                            with(get(index)) {
                                text = "$letter"

                                if (action == Action.Add) {
                                    if (index == letters.lastIndex) {
                                        scale(1.5F, 240L) {
                                            scale(1.0F, 240L)
                                        }
                                    }
                                }
                            }
                        }

                        if (action == Action.Remove) {
                            repeat(Word.LENGTH - letters.length) {
                                get(Word.LENGTH.dec() - it).text = BLANK
                            }
                        }
                    }
                }
            }
        }

        private fun LettersItemBinding.get(index: Int) = when(index) {
            0 -> firstLetter
            1 -> secondLetter
            2 -> thirdLetter
            3 -> fourthLetter
            4 -> fifthLetter
            else -> throw IllegalArgumentException("index :$index")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = LettersItemBinding.inflate(inflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback: DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem.index == newItem.index
        }

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem == newItem
        }
    }
}

sealed class AdapterItem {
    abstract val index: Int

    data class Letters(
        override val index: Int,
        val letters: String,
        val previousLetters: String,
        val submitted: Boolean = false
    ) : AdapterItem() {
        val action: Action
            get() = if (letters.length > previousLetters.length) {
                Action.Add
            } else {
                Action.Remove
            }

        companion object {
            fun from(index: Int, letters: PresentationModel) = Letters(
                index = index,
                letters = letters.letters,
                previousLetters = letters.previousLetters
            )
        }
    }
}

enum class Action {
    Add,
    Remove
}