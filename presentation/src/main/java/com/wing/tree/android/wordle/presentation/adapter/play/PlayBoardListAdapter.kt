package com.wing.tree.android.wordle.presentation.adapter.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.android.wordle.presentation.databinding.LineItemBinding
import com.wing.tree.android.wordle.presentation.extention.scale
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.PlayBoard
import com.wing.tree.android.wordle.presentation.widget.LetterView.Flag
import com.wing.tree.android.wordle.presentation.model.play.Line as PresentationLine


class PlayBoardListAdapter(private val callbacks: Callbacks) : ListAdapter<AdapterItem, PlayBoardListAdapter.ViewHolder>(DiffCallback()) {
    private var round = 0
    private var runsAnimation = false

    interface Callbacks {
        fun beforeAnimationStart()
        fun onLetterClick(adapterPosition: Int, index: Int)
        fun onAnimationEnd()
    }

    inner class ViewHolder(private val viewBinding: LineItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.Line -> with(viewBinding.lineView) {
                    isCurrentLine = adapterPosition == round

                    if (item.isSubmitted) {
                        setOnLetterClickListener(null)

                        val flag = if (runsAnimation) {
                            Flag.Submit
                        } else {
                            Flag.Restore
                        }

                        submitLetters(item.currentLetters, flag)

                        if (runsAnimation) {
                            callbacks.beforeAnimationStart()
                            flipAll { callbacks.onAnimationEnd() }
                        }
                    } else {
                        setOnLetterClickListener { letterView, index ->
                            with(letterView) {
                                scale(1.0F, 1.15F, 150L) {
                                    scale(1.15F, 1.0F, 150L)
                                }
                            }

                            callbacks.onLetterClick(adapterPosition, index)
                        }

                        item.currentLetters.zip(item.previousLetters).forEachIndexed { index, (currentLetter, previousLetter) ->
                            if (currentLetter.isSubmitted) {
                                val featureFlag = if (runsAnimation) {
                                    Flag.Submit
                                } else {
                                    Flag.Restore
                                }

                                get(index).submitLetter(currentLetter, featureFlag)

                                if (runsAnimation) {
                                    callbacks.beforeAnimationStart()
                                    flipAt(index) {
                                        it.isFlippable = false

                                        callbacks.onAnimationEnd()
                                    }
                                } else {
                                    get(index).isFlippable = false
                                }
                            } else {
                                val flag = if (runsAnimation) {
                                    val action = when {
                                        item.isAdded(index) -> Flag.Action.Add
                                        item.isRemoved(index) -> Flag.Action.Remove
                                        else -> Flag.Action.Nothing
                                    }

                                    Flag.Default(action)
                                } else {
                                    Flag.Restore
                                }

                                get(index).submitLetter(currentLetter, flag)
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
        val list = playBoard.lines.mapIndexed { index, line ->
            AdapterItem.Line.from(index, line)
        }

        round = playBoard.round
        runsAnimation = playBoard.runsAnimation.get()

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
        val currentLetters: Array<Letter>,
        val previousLetters: Array<Letter>,
        val isSubmitted: Boolean = false
    ) : AdapterItem() {
        fun isAdded(index: Int) = currentLetters[index].isNotBlank && previousLetters[index].isBlank
        fun isRemoved(index: Int) = currentLetters[index].isBlank && previousLetters[index].isNotBlank

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Line) return false

            if (index != other.index) return false
            if (!currentLetters.contentEquals(other.currentLetters)) return false
            if (!previousLetters.contentEquals(other.previousLetters)) return false
            if (isSubmitted != other.isSubmitted) return false

            return true
        }

        override fun hashCode(): Int {
            var result = index
            result = 31 * result + currentLetters.contentHashCode()
            result = 31 * result + previousLetters.contentHashCode()
            result = 31 * result + isSubmitted.hashCode()
            return result
        }

        companion object {
            fun from(index: Int, line: PresentationLine) = Line(
                index = index,
                currentLetters = line.currentLetters.copyOf(),
                previousLetters = line.previousLetters.copyOf(),
                isSubmitted = line.isSubmitted
            )
        }
    }
}