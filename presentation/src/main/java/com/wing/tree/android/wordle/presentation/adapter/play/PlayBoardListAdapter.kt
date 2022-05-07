package com.wing.tree.android.wordle.presentation.adapter.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.android.wordle.presentation.databinding.LineItemBinding
import com.wing.tree.android.wordle.presentation.model.play.PlayBoard
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.widget.LetterView
import com.wing.tree.android.wordle.presentation.model.play.Line as Model

class PlayBoardListAdapter(private val callbacks: Callbacks) : ListAdapter<AdapterItem, PlayBoardListAdapter.ViewHolder>(DiffCallback()) {
    private var isRestored = false

    interface Callbacks {
        fun onLetterClick(adapterPosition: Int, index: Int)
        fun onAnimationEnd()
        fun onAnimationStart()
    }

    inner class ViewHolder(private val viewBinding: LineItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.Line -> with(viewBinding.lineView) {
                    if (item.isSubmitted) {
                        setOnLetterClickListener(null)

                        val featureFlag = if (isRestored) {
                            LetterView.FeatureFlag.Restore
                        } else {
                            LetterView.FeatureFlag.Submit
                        }

                        submitLetters(item.letters, featureFlag)

                        if (isRestored.not()) {
                            callbacks.onAnimationStart()
                            flipAll { callbacks.onAnimationEnd() }
                        }
                    } else {
                        setOnLetterClickListener { _, index ->
                            callbacks.onLetterClick(adapterPosition, index)
                        }

                        item.letters.zip(item.previousLetters).forEachIndexed { index, (letter, previousLetter) ->
                            if (letter.isSubmitted) {
                                val featureFlag = if (isRestored) {
                                    LetterView.FeatureFlag.Restore
                                } else {
                                    LetterView.FeatureFlag.Submit
                                }

                                get(index).submitLetter(letter, featureFlag)

                                if (isRestored.not()) {
                                    flipAt(index) { it.isFlippable = false }
                                } else {
                                    get(index).isFlippable = false
                                }
                            } else {
                                val featureFlag = if (isRestored) {
                                    LetterView.FeatureFlag.Restore
                                } else {
                                    LetterView.FeatureFlag.Normal
                                }

                                get(index).submitLetter(letter, featureFlag)

                                if (previousLetter.isBlank && letter.isNotBlank && isRestored.not()) {
                                    scaleAt(index)
                                } else if (previousLetter.isNotBlank && letter.isBlank) {
                                    // 딜리트.. 페이딩이나,, 등등.
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
        val viewBinding = LineItemBinding.inflate(inflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun submitPlayBoard(playBoard: PlayBoard, commitCallback: Runnable? = null) {
        val list = playBoard.lines.mapIndexed { index, letters ->
            AdapterItem.Line.from(index, letters)
        }

        isRestored = playBoard.isRestored.compareAndSet(true, false)

        submitList(list, commitCallback)
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

    data class Line(
        override val index: Int,
        val letters: Array<Letter>,
        val previousLetters: Array<Letter>,
        val isSubmitted: Boolean = false
    ) : AdapterItem() {
        companion object {
            fun from(index: Int, letters: Model) = Line(
                index = index,
                letters = letters.letters.copyOf(),
                previousLetters = letters.previousLetters.copyOf(),
                isSubmitted = letters.isSubmitted
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Line) return false

            if (index != other.index) return false
            if (!letters.contentEquals(other.letters)) return false
            if (!previousLetters.contentEquals(other.previousLetters)) return false
            if (isSubmitted != other.isSubmitted) return false

            return true
        }

        override fun hashCode(): Int {
            var result = index
            result = 31 * result + letters.contentHashCode()
            result = 31 * result + previousLetters.contentHashCode()
            result = 31 * result + isSubmitted.hashCode()
            return result
        }
    }
}