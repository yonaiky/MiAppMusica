package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R

class ExportComponent private constructor(
    private val exportState: MutableState<Boolean>
): MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun init( exportState: MutableState<Boolean> ) =
            ExportComponent(exportState)
    }

    var isExport: Boolean = exportState.value
        set(value) {
            exportState.value = value
            field = value
        }
    override val title: String
        @Composable
        get() = stringResource( R.string.export_playlist )
    override val iconId: Int = R.drawable.export

    override fun onShortClick() { isExport = true }
}