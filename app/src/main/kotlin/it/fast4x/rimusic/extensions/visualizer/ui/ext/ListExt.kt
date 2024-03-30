package it.fast4x.rimusic.extensions.visualizer.ui.ext

fun <T> List<T>.repeat(times: Int): MutableList<T> {
    val result = mutableListOf<T>()
    repeat(times) {
        result.addAll(this)
    }
    return result
}