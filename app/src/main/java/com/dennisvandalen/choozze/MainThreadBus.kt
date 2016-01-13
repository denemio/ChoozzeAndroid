package com.dennisvandalen.choozze

import android.os.Handler
import android.os.Looper
import com.squareup.otto.Bus

/**
 * MainThreadBus, we want our eventbus posts to be handled on our MainThread :)
 */
class MainThreadBus : Bus() {
    private val mHandler = Handler(Looper.getMainLooper())

    override fun post(event: Any) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event)
        } else {
            mHandler.post { super@MainThreadBus.post(event) }
        }
    }
}