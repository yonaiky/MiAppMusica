package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R

class ImportComponent private constructor(
    private val onClick: () -> Unit
): MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun init( onClick: () -> Unit ) =
            ImportComponent( onClick )
    }

    override val menuIconTitle: String
        @Composable
        get() = stringResource( R.string.import_playlist )
    override val iconId: Int = R.drawable.resource_import

    override fun onShortClick() { onClick() }
}