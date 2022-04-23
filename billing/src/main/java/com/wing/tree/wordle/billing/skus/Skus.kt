package com.wing.tree.wordle.billing.skus

internal object Skus {
    const val GOLD_240 = "gold_240"
    const val GOLD_720 = "gold_720"
    const val REMOVE_ADS = "remove_ads"

    val consumableList = listOf(GOLD_240, GOLD_720)
    val list = listOf(GOLD_240, GOLD_720, REMOVE_ADS)
}