package com.dennisvandalen.choozze

import com.dennisvandalen.choozze.event.JsoupFailedEvent
import com.dennisvandalen.choozze.event.UsageEvent
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*

class ChoozzeUsageFetcher(private val login_username: String, private val password: String) : Thread() {

    /**
     * Fetch usage data
     */
    override fun run() {
        var doc: Document? = null

        try {
            val data = HashMap<String, String>()
            data.put("login-username", login_username)
            data.put("password", password)
            data.put("remember", "on")
            data.put("action", "login")

            doc = ChoozzeApplication.httpClient.post("https://choozze.me/login.php", data)

            val statusEvent = UsageEvent(doc)
            ChoozzeApplication.bus.post(statusEvent)

        } catch (e: IOException) {
            ChoozzeApplication.bus.post(JsoupFailedEvent())
        }

    }
}