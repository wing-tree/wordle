package com.wing.tree.wordle.billing.skus

internal object Skus {
    const val CREDITS_240 = "credits_240"
    const val GOLD_240 = "240"
    const val GOLD_720 = "720"
    const val REMOVE_ADS = "remove_ads"

    val consumableList = listOf(GOLD_240, GOLD_720)
    val list = listOf(CREDITS_240, GOLD_240, GOLD_720, REMOVE_ADS)
}