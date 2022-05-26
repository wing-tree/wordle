package com.wing.tree.android.wordle.presentation.eventbus

import com.wing.tree.android.wordle.domain.util.notNull
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter

class EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun produceEvent(event: Event) {
        _events.emit(event)
    }

    suspend inline fun <reified T: Event> subscribeEvent(collector: FlowCollector<Event>) {
        events.filter { it is T }.collect(collector)
    }

    companion object {
        private var INSTANCE: EventBus? = null

        fun getInstance(): EventBus {
            val eventBus = INSTANCE

            return if (eventBus.notNull()) {
                eventBus
            } else {
                EventBus().also {
                    INSTANCE = it
                }
            }
        }
    }
}