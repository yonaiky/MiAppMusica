package it.fast4x.rimusic.enums

enum class PlayerThumbnailSize {
    Small,
    Medium,
    Big,
    Biggest;

    val size: Int
        get() = when (this) {
            Small -> 80
            Medium -> 45
            Big -> 20
            Biggest -> 10
        }

}
