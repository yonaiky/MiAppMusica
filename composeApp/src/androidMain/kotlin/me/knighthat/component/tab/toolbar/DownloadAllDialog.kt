package me.knighthat.component.tab.toolbar

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import it.fast4x.rimusic.R
import it.fast4x.rimusic.service.PlayerService
import it.fast4x.rimusic.utils.manageDownload

@UnstableApi
interface DownloadAllDialog: ConfirmationDialog {

    override val context: Context
    val binder: PlayerService.Binder?
    override val toggleState: MutableState<Boolean>
    val downloadState: MutableIntState

    override val iconId: Int
        get() = R.drawable.downloaded
    override val titleId: Int
        get() = R.string.do_you_really_want_to_download_all
    override val messageId: Int
        get() = R.string.info_download_all_songs

    fun listToProcess(): List<MediaItem>

    override fun onConfirm() {
        downloadState.intValue = Download.STATE_DOWNLOADING

        listToProcess().forEach {
            binder?.cache?.removeResource(it.mediaId)

            manageDownload(
                context = context,
                songId = it.mediaId,
                songTitle = it.mediaMetadata.title.toString(),
                downloadState = false
            )
        }

        onDismiss()
    }
}