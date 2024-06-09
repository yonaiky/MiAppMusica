package it.fast4x.rimusic.models

import io.ktor.http.Url

data class PipedSession(
    val instanceName: String,
    val apiBaseUrl: Url,
    val token: String,
    val username: String
) {

    fun serialize() = "$instanceName|$apiBaseUrl|$token|$username"
    companion object {
        fun deserialize(serialized: String) = serialized.split("|").let {
            PipedSession(it[0], Url(it[1]), it[2], it[3])
        }
    }
}

val String.toPipedSession: PipedSession
    get() = this.split("|").let {
        PipedSession(it[0], Url(it[1]), it[2], it[3])
    }