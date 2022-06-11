@file:Suppress("unused")

package com.wing.tree.wordle.core.exception

class NotEnoughCreditException: Exception {
    constructor(): super()
    constructor(message: String): super(message)
    @Suppress("UNUSED_PARAMETER")
    constructor(message: String, cause: Throwable): super(message)
    constructor(cause: Throwable): super(cause)
}