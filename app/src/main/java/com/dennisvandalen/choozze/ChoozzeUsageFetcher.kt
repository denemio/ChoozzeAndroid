package com.dennisvandalen.choozze

import com.dennisvandalen.choozze.event.JsoupFailedEvent
import com.dennisvandalen.choozze.event.UsageEvent

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.io.IOException

class ChoozzeUsageFetcher(private val login_username: String, private val password: String) : Thread() {

    /**
     * Fetch usage data
     */
    override fun run() {
        var doc: Document? = null

        try {
            doc = Jsoup.connect("https://choozze.me/login.php")
                    .data("login-username", login_username)
                    .data("password", password)
                    .data("action", "login")
                    .userAgent("Mozilla")
                    .post()

            val statusEvent = UsageEvent(doc)
            ChoozzeApplication.bus.post(statusEvent)

        } catch (e: IOException) {
            ChoozzeApplication.bus.post(JsoupFailedEvent())
        }

    }
}