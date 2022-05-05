package com.wing.tree.wordle.core.exception

import java.io.IOException

class WordNotFoundException: IOException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message)
    constructor(cause: Throwable): super(cause)
}