package com.wing.tree.wordle.billing.skus

object Skus {
    const val CREDITS_240 = "credits_240"
    const val CREDITS_720 = "credits_720"
    const val CREDITS_2000 = "credits_2000"
    const val CREDITS_6000 = "credits_6000"
    const val REMOVE_ADS = "remove_ads"

    val all = listOf(CREDITS_240, CREDITS_720, CREDITS_2000, CREDITS_6000, REMOVE_ADS)
    val consumable = listOf(CREDITS_240, CREDITS_720, CREDITS_2000, CREDITS_6000)

    val credits = hashMapOf(
        CREDITS_240 to 240,
        CREDITS_720 to 720,
        CREDITS_2000 to 2000,
        CREDITS_6000 to 6000
    )

    val orders = HashMap<String, Int>(all.size).apply {
        all.forEachIndexed { index, sku ->
            put(sku, index)
        }
    }
}