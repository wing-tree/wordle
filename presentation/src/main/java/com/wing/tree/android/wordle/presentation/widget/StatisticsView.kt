package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.wing.tree.android.wordle.domain.model.Guesses
import com.wing.tree.android.wordle.domain.model.Statistics
import com.wing.tree.android.wordle.domain.util.float
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.StatisticsViewBinding
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import java.util.*

class StatisticsView : ConstraintLayout {
    private val viewBinding = StatisticsViewBinding.bind(inflate(context, R.layout.statistics_view, this))

    var statistics: Statistics? = null
        set(value) {
            field = value

            with(viewBinding) {
                field?.let {
                    played.textViewLabel.text = getString(R.string.played)
                    played.textViewValue.text = "${it.played}"

                    won.textViewLabel.text = getString(R.string.won)
                    won.textViewValue.text = "${it.won}"

                    winningStreak.textViewLabel.text = getString(R.string.winning_streak)
                    winningStreak.textViewValue.text = "${it.winningStreak}"

                    maximumWinStreak.textViewLabel.text = getString(R.string.maximum_win_streak)
                    maximumWinStreak.textViewValue.text = "${it.maximumWinStreak}"

                    initHorizontalBarChart(horizontalBarChart, it.guesses)
                }
            }
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun getString(@StringRes resId: Int) = context.getString(resId)

    private fun initHorizontalBarChart(horizontalBarChart: HorizontalBarChart, guesses: Guesses) {
        val barDataSet: BarDataSet
        val barEntries = ArrayList<BarEntry>()
        val color = context.getColor(R.color.letter_matched)
        val textColor = context.getColor(R.color.text)

        repeat(MAXIMUM_ROUND) {
            val index = it.inc()
            val x = index.float
            val y = guesses[index].float

            barEntries.add(BarEntry(x, y))
        }

        barDataSet = BarDataSet(barEntries, BLANK)

        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}"
            }
        }

        barDataSet.color = color
        barDataSet.valueTextColor = textColor
        barDataSet.valueTextSize = 16.0F

        with(horizontalBarChart) {
            data = BarData(barDataSet)
            description.isEnabled = false
            legend.isEnabled = false

            setDrawGridBackground(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            setTouchEnabled(false)

            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawLabels(false)

            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            axisRight.setDrawLabels(false)

            data.barWidth = 0.25F

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(true)
            xAxis.textColor = textColor
            xAxis.textSize = 20.0F

            invalidate()
        }
    }

    private operator fun Guesses.get(index: Int) = when(index) {
        1 -> one
        2 -> two
        3 -> three
        4 -> four
        5 -> five
        else -> sixOrMore
    }
}