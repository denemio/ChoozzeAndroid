package com.dennisvandalen.choozze

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class JsoupClient {
    val cookies = HashMap<String, String>();

    private fun connect(url: String): Connection {
        val connection = Jsoup.connect(url);

        for (cookie in cookies) {
            connection.cookie(cookie.key, cookie.value);
        }

        return connection!!
    }

    public fun post(url: String, data: HashMap<String, String>): Document {
        val request = connect(url);
        request.data(data);

        val response = request.method(Connection.Method.POST).execute();

        cookies.putAll(response.cookies());
        return response.parse();
    }

    public fun get(url: String, data: HashMap<String, String>): Document {
        val request = connect(url);
        request.data(data);

        val response = request.method(Connection.Method.GET).execute();

        cookies.putAll(response.cookies());
        return response.parse();
    }

}