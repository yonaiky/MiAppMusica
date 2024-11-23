package it.fast4x.rimusic.enums

enum class MusicAnimationType {
    Bars,
    CrazyBars,
    CrazyPoints,
    Bubbles;

    val textName: String
        get() = when(this) {
            Bars -> "Bars"
            CrazyBars -> "Crazy Bars"
            CrazyPoints -> "Crazy Points"
            Bubbles -> "Bubbles"
        }
}