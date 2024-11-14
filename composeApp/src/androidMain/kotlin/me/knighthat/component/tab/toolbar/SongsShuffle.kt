package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.utils.PlayShuffledSongs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.knighthat.appContext
import kotlin.coroutines.CoroutineContext

@UnstableApi
class SongsShuffle private constructor(
    private val binder: PlayerServiceModern.Binder?,
    private val dispatcher: CoroutineContext,
    private val songs: () -> Flow<List<Song>?>
): Icon, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init(
            dispatcher: CoroutineContext = Dispatchers.IO,
            songs: () -> Flow<List<Song>?>
        ) = SongsShuffle(
                LocalPlayerServiceBinder.current,
                dispatcher,
                songs
            )
    }

    override val iconId: Int = R.drawable.shuffle
    override val textId: Int = R.string.shuffle

    override fun onShortClick() {
        CoroutineScope( dispatcher ).launch {
            songs().collect {
                PlayShuffledSongs( it, appContext(), binder )
            }
        }
    }
}