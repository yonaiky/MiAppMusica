package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R

enum class AlbumsType {
    Favorites,
    All;

    val textName: String
        @Composable
        get() = when( this ) {
            Favorites -> stringResource(R.string.favorites)
            All -> stringResource(R.string.all)
        }

}