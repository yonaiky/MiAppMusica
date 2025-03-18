package me.knighthat.database

import androidx.room.Dao
import androidx.room.RewriteQueriesToDropUnusedColumns
import it.fast4x.rimusic.models.SearchQuery

@Dao
@RewriteQueriesToDropUnusedColumns
interface SearchQueryTable: SqlTable<SearchQuery> {

    override val tableName: String
        get() = "SearchQuery"
}