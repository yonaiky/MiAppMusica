package me.knighthat.component.tab.toolbar

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.ui.components.themed.InputTextDialog
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@UnstableApi
interface ExportSongsToCSVDialog: InputDialog {

    companion object {

        fun fileName( fromUser: String ): String  {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault() )
            return "RMPlaylist_${fromUser.take( 20 )}_${dateFormat.format( Date() )}"
        }

        fun toFile(
            uri: Uri,
            context: Context,
            browseId: String,
            playlistName: String,
            listToProcess: List<MediaItem>
        ) {
            context.applicationContext
                .contentResolver
                .openOutputStream(uri)
                ?.use { outputStream ->
                    csvWriter().open(outputStream) {
                        writeRow(
                            "PlaylistBrowseId",
                            "PlaylistName",
                            "MediaId",
                            "Title",
                            "Artists",
                            "Duration",
                            "ThumbnailUrl"
                        )
                        listToProcess.forEach { song ->
                            val duration = song.mediaMetadata.durationMs?.div( 1000f )

                            writeRow(
                                browseId,
                                playlistName,
                                song.mediaId,
                                song.mediaMetadata.title,
                                song.mediaMetadata.artist,
                                duration?.let { "%.0f".format(it) } ?: "",
                                song.mediaMetadata.artworkUri
                            )
                        }
                    }
                }
        }
    }

    override val iconId: Int
        @DrawableRes
        get() = R.drawable.resource_import
    override val titleId: Int
        @StringRes
        get() = R.string.enter_the_playlist_name
    override val messageId: Int
        get() = R.string.export_playlist

    @Composable
    override fun Render() {
        if( !toggleState.value ) return

        InputTextDialog(
            onDismiss = ::onDismiss,
            title = stringResource( this.titleId ),
            value = this.defValue,
            placeholder = stringResource( this.placeHolder ),
            setValue = {
                try {
                    onSet( it )
                } catch ( _: ActivityNotFoundException ) {
                    SmartMessage(
                        "Couldn't find an application to create documents",
                        type = PopupType.Warning,
                        context = context
                    )
                } finally { onDismiss() }
            }
        )
    }
}