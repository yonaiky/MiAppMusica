package it.fast4x.rimusic.enums

enum class SortOrder( val rotationZ: Float ) {
    Ascending( 0f ),
    Descending( 180f );

    operator fun not() = when (this) {
        Ascending -> Descending
        Descending -> Ascending
    }
}
