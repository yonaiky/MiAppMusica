package app.kreate.android.utils.innertube

import it.fast4x.rimusic.EXPLICIT_PREFIX
import it.fast4x.rimusic.models.Song
import me.knighthat.innertube.model.InnertubeSong

val InnertubeSong.toSong: Song
    get() = Song(
        id = this.id,
        title = if( isExplicit ) EXPLICIT_PREFIX else "" + this.name,
        artistsText = this.authorsText,
        durationText = this.durationText,
        thumbnailUrl = this.thumbnails.firstOrNull()?.url,
        likedAt = null,
        totalPlayTimeMs = 0
    )