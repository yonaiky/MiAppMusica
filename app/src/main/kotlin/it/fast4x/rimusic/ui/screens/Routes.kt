package it.fast4x.rimusic.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.media3.common.util.UnstableApi
import it.fast4x.compose.routing.Route0
import it.fast4x.compose.routing.Route1
import it.fast4x.compose.routing.Route2
import it.fast4x.compose.routing.RouteHandlerScope
import it.fast4x.rimusic.enums.BuiltInPlaylist
import it.fast4x.rimusic.enums.DeviceLists
import it.fast4x.rimusic.enums.SearchType
import it.fast4x.rimusic.enums.StatisticsType
import it.fast4x.rimusic.models.Mood
import it.fast4x.rimusic.ui.screens.album.AlbumScreenWithoutScaffold
import it.fast4x.rimusic.ui.screens.artist.ArtistScreen
import it.fast4x.rimusic.ui.screens.home.HomeScreen
import it.fast4x.rimusic.ui.screens.playlist.PlaylistScreen
import it.fast4x.rimusic.ui.screens.localplaylist.LocalPlaylistScreen
import it.fast4x.rimusic.ui.screens.mood.MoodScreen
import it.fast4x.rimusic.ui.screens.ondevice.DeviceListSongsScreen
import it.fast4x.rimusic.ui.screens.search.SearchTypeScreen
import it.fast4x.rimusic.ui.screens.statistics.StatisticsScreen

val quickpicksRoute = Route1<String?>("quickpicksRoute")
val albumRoute = Route1<String?>("albumRoute")
val artistRoute = Route1<String?>("artistRoute")
val builtInPlaylistRoute = Route1<BuiltInPlaylist>("builtInPlaylistRoute")
val deviceListSongRoute = Route1<String>("deviceListSongRoute")
val statisticsTypeRoute = Route1<StatisticsType>("statisticsTypeRoute")
val localPlaylistRoute = Route1<Long?>("localPlaylistRoute")
val searchResultRoute = Route1<String>("searchResultRoute")
val searchRoute = Route1<String>("searchRoute")
val searchTypeRoute = Route1<SearchType>("searchTypeRoute")
val settingsRoute = Route0("settingsRoute")
val homeRoute = Route0("homeRoute")
val moodRoute = Route1<Mood>("moodRoute")
//val playlistRoute = Route1<String?>("playlistRoute")
//val playlistRoute = Route3<String?, String?, Int?>("playlistRoute")
val playlistRoute = Route2<String?, String?>("playlistRoute")

@ExperimentalMaterialApi
@ExperimentalTextApi
@SuppressLint("ComposableNaming")
@Suppress("NOTHING_TO_INLINE")
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@UnstableApi
@Composable
inline fun RouteHandlerScope.globalRoutes() {
    albumRoute { browseId ->
        AlbumScreenWithoutScaffold(
            browseId = browseId ?: error("browseId cannot be null")
        )
        /*
        AlbumScreen(
            browseId = browseId ?: error("browseId cannot be null")
        )
         */
    }

    artistRoute { browseId ->
        ArtistScreen(
            browseId = browseId ?: error("browseId cannot be null")
        )
    }

/*
    playlistRoute { browseId ->
        PlaylistScreen(
        )
    }
 */

    localPlaylistRoute { playlistId ->
        LocalPlaylistScreen(
            playlistId = playlistId ?: error("playlistId cannot be null")
        )
    }


    playlistRoute { browseId, params ->
        PlaylistScreen(
            browseId = browseId ?: error("browseId cannot be null"),
            params = params
        )
    }

    statisticsTypeRoute { browseId ->
        StatisticsScreen(
            statisticsType = browseId ?: error("browseId cannot be null")
        )
    }

    searchTypeRoute { browseId ->
        SearchTypeScreen(
            searchType = browseId ?: error("browseId cannot be null")
        )
    }

    homeRoute {
        HomeScreen(
            onPlaylistUrl = {pop},
            openTabFromShortcut = -1
        )
    }

    moodRoute { mood ->
        MoodScreen(mood = mood)
    }


    deviceListSongRoute { browseId ->
        DeviceListSongsScreen(
            deviceLists = DeviceLists.LocalSongs
        )
    }

    quickpicksRoute { browseId ->

    }

}
