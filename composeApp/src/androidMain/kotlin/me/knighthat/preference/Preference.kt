package me.knighthat.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import me.knighthat.enums.HomeItemSize

object Preference {

    /****  ENUMS  ****/
    val HOME_ARTIST_ITEM_SIZE = Key( "AristItemSizeEnum", HomeItemSize.SMALL )
    val HOME_ALBUM_ITEM_SIZE = Key( "AlbumItemSizeEnum", HomeItemSize.SMALL )
    val HOME_LIBRARY_ITEM_SIZE = Key( "LibraryItemSizeEnum", HomeItemSize.SMALL )

    @Composable
    inline fun <reified T: Enum<T>> remember( key: Key<T> ): MutableState<T> =
        it.fast4x.rimusic.utils.rememberPreference( key.key, key.default )

    /**
     * In order to ensure consistent between input key and output value.
     * The provided key must bear a potential return value.
     */
    data class Key<T>( val key: String, val default: T )
}