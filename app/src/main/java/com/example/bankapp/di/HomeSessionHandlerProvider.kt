package com.example.bankapp.di

import com.example.bankapp.usecases.CurrentSessionIntent
import com.example.bankapp.usecases.HomeSessionHandler

object HomeSessionHandlerProvider {
    private var _currentHandler: HomeSessionHandler? = null

    val currentHandler: HomeSessionHandler
        get() = _currentHandler ?: throw IllegalStateException(
            "Transaction handler not initialized. Call setHandler first..."
        )

    fun setHandler(handler: HomeSessionHandler) {
        _currentHandler = handler
    }

    fun setHandlerByIntent(intent: CurrentSessionIntent) {
        _currentHandler = HomeSessionHandler.getInstance(intent)
    }

    fun reset() {
        _currentHandler = null
    }

   // fun isInitialized(): Boolean = _currentHandler != null
}