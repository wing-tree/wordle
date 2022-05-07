package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
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

    private var statistics: Statistics? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setStatistics(statistics: Statistics) {
        this.statistics = statistics

        val played = "PLAYED ${statistics.played}"
        val maximumWinStreak = "MAXIMUM WIN STREAK ${statistics.maximumWinStreak}"
        val winningStreak = "WINNING STREAK ${statistics.winningStreak}"
        val won = "WON ${statistics.won}"

        with(viewBinding) {
            textViewMaximumWinStreak.text = maximumWinStreak
            textViewPlayed.text = played
            textViewWinningStreak.text = winningStreak
            textViewWon.text = won

            initHorizontalBarChart(horizontalBarChart, statistics.guesses)
        }
    }

    private fun initHorizontalBarChart(horizontalBarChart: HorizontalBarChart, guesses: Guesses) {
        val barEntries = ArrayList<BarEntry>()

        repeat(MAXIMUM_ROUND) {
            val index = it.inc()

            barEntries.add(BarEntry(index.float, guesses[index].float))
        }

        val barDataSet = BarDataSet(barEntries, BLANK)

        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 20.0F

        with(horizontalBarChart) {
            data = BarData(barDataSet)
            description.isEnabled = false
            legend.isEnabled = false

            setDrawGridBackground(false)
            setPinchZoom(false)

            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawLabels(false)

            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            axisRight.setDrawLabels(false)

            data.barWidth = 0.25F

            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(true)
            xAxis.textSize = 20F

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