package com.dennisvandalen.choozze

import android.app.Application

/**
 * Application class, containing our bus
 */
class ChoozzeApplication : Application() {
    companion object {
        @JvmField val httpClient: JsoupClient = JsoupClient()
        @JvmField val bus: MainThreadBus = MainThreadBus()
    }
}

