package com.wing.tree.android.wordle.presentation.adapter.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.databinding.LettersItemBinding
import com.wing.tree.android.wordle.presentation.model.play.Board
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.Line as Model

class BoardListAdapter(private val callbacks: Callbacks) : ListAdapter<AdapterItem, BoardListAdapter.ViewHolder>(DiffCallback()) {
    interface Callbacks {
        fun onLetterClick(adapterPosition: Int, index: Int)
        fun onAnimationEnd()
        fun onAnimationStart()
    }

    inner class ViewHolder(private val viewBinding: LettersItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.Letters -> with(viewBinding.lettersView) {
                    with(item) {
                        if (submitted) {
                            setOnLetterClickListener(null)

                            letters.forEachIndexed { index, letter ->
                                set(index, letter)
                            }

                            callbacks.onAnimationStart()
                            flip { callbacks.onAnimationEnd() }
                        } else {
                            setOnLetterClickListener { _, index ->
                                callbacks.onLetterClick(adapterPosition, index)
                            }

                            letters.zip(previousLetters).forEachIndexed { index, (letter, previousLetter) ->
                                set(index, letter)

                                if (letter.submitted) {
                                    with(get(index)) {
                                        if (flippable) {
                                            front.text = BLANK
                                            flipAt(index) { flippable = false }
                                        }
                                    }
                                } else {
                                    if (previousLetter.isBlank && letter.isNotBlank) {
                                        scaleAt(index)
                                    } else if (previousLetter.isNotBlank && letter.isBlank) {

                                    }
                                }
                            }
                        }
                    }
                }
            }
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

    fun submitBoard(board: Board) {
        val list = board.lines.mapIndexed { index, letters ->
            AdapterItem.Letters.from(index, letters)
        }

        submitList(list)
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
        val letters: Array<Letter>,
        val previousLetters: Array<Letter>,
        val submitted: Boolean = false
    ) : AdapterItem() {
        companion object {
            fun from(index: Int, letters: Model) = Letters(
                index = index,
                letters = letters.letters.copyOf(),
                previousLetters = letters.previousLetters.copyOf(),
                submitted = letters.submitted
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Letters) return false

            if (index != other.index) return false
            if (!letters.contentEquals(other.letters)) return false
            if (!previousLetters.contentEquals(other.previousLetters)) return false
            if (submitted != other.submitted) return false

            return true
        }

        override fun hashCode(): Int {
            var result = index
            result = 31 * result + letters.contentHashCode()
            result = 31 * result + previousLetters.contentHashCode()
            result = 31 * result + submitted.hashCode()
            return result
        }
    }
}