package com.dennisvandalen.choozze

import android.app.Application

/**
 * Application class, containing our bus
 */
class ChoozzeApplication : Application() {
    companion object ChoozzeBus {
        val bus: MainThreadBus = MainThreadBus()
    }
}

