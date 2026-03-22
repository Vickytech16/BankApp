package com.example.bankapp.services

import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.HomeSessionHandler

object HomeSessionHandlerManager {
    private var _currentHandler: HomeSessionHandler? = null

    val currentHandler: HomeSessionHandler
        get() = _currentHandler ?: throw IllegalStateException(
            "Transaction handler not initialized. Call setHandler() first."
        )

    fun setHandler(handler: HomeSessionHandler) {
        _currentHandler = handler
    }

    fun setHandlerByIntent(intent: CurrentSessionIntent) {
        _currentHandler = HomeSessionHandler.Companion.getInstance(intent)
    }

    fun reset() {
        _currentHandler = null
    }

    fun isInitialized(): Boolean = _currentHandler != null
}