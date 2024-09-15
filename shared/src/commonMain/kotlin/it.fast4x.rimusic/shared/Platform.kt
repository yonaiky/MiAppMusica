package it.fast4x.rimusic.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


