package com.wing.tree.wordle.core.exception

class NotEnoughCreditException: Exception {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message)
    constructor(cause: Throwable): super(cause)
}