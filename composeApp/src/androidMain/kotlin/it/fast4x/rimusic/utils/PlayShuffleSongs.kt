package it.fast4x.rimusic.utils

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.enums.MaxSongs
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.service.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
fun PlayShuffledSongs(songsList: List<Song>?, context: Context, binder: PlayerService.Binder?) {

    if (songsList == null || binder == null) return

    val maxSongsInQueue = context.preferences.getEnum(maxSongsInQueueKey, MaxSongs.`500`)

    songsList.let { songs ->
        if (songs.isNotEmpty() == true) {
            val itemsLimited =
                if (songs.size > maxSongsInQueue.number) songs.shuffled()
                    .take(maxSongsInQueue.number.toInt()) else songs

            CoroutineScope(Dispatchers.Main).launch {
                binder.stopRadio()
                binder.player.forcePlayFromBeginning(
                    itemsLimited.shuffled()
                        .map(Song::asMediaItem)
                )
            }
        }
    }
}