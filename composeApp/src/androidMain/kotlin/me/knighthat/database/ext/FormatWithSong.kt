package me.knighthat.database.ext

import androidx.room.Embedded
import androidx.room.Relation
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.models.Song

data class FormatWithSong(
    @Embedded val format: Format,
    @Relation(
        parentColumn = "songId",
        entityColumn = "id",
    )
    val song: Song
)