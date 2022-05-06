package com.wing.tree.wordle.billing.skus

object Skus {
    const val CREDITS_240 = "credits_240"
    const val CREDITS_720 = "credits_720"
    const val REMOVE_ADS = "remove_ads"

    val consumableList = listOf(CREDITS_240, CREDITS_720)
    val list = listOf(CREDITS_240, REMOVE_ADS, CREDITS_720)
}