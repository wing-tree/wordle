package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.wing.tree.android.wordle.domain.model.staticstics.Guesses
import com.wing.tree.android.wordle.domain.model.staticstics.Statistics
import com.wing.tree.android.wordle.domain.util.float
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.StatisticsViewBinding
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import com.wing.tree.wordle.core.util.int

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
        val typeface = ResourcesCompat.getFont(context, R.font.font_family_maplestory)

        repeat(MAXIMUM_ROUND) {
            val index = it.inc()
            val x = index.float
            val y = guesses[index].float

            barEntries.add(BarEntry(x, y))
        }

        barDataSet = BarDataSet(barEntries, BLANK)
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.int}"
            }
        }

        barDataSet.color = color
        barDataSet.valueTextColor = textColor
        barDataSet.valueTextSize = 14.0F
        barDataSet.valueTypeface = typeface

        with(horizontalBarChart) {
            setDrawGridBackground(false)
            setFitBars(true)
            setPinchZoom(false)
            setScaleEnabled(false)
            setTouchEnabled(false)

            axisLeft.axisMinimum = 0.0F
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawLabels(false)

            axisRight.axisMinimum = 0.0F
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            axisRight.setDrawLabels(false)

            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = textColor
            xAxis.textSize = 18.0F
            xAxis.typeface = typeface
            xAxis.xOffset = 12.0F

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val int = value.int

                    return if (int > MAXIMUM_ROUND.dec()) {
                        "+$int"
                    } else {
                        "$int"
                    }
                }
            }

            data = BarData(barDataSet)
            data.barWidth = 0.25F
            description.isEnabled = false
            legend.isEnabled = false

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