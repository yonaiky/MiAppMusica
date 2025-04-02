package it.fast4x.rimusic.utils

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import app.kreate.android.R
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.MONTHLY_PREFIX
import it.fast4x.rimusic.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.knighthat.utils.TimeDateUtils
import timber.log.Timber
import java.time.LocalDate

@Composable
fun CheckMonthlyPlaylist() {
    val ym = getCalculatedMonths(1)
    val y = ym.substring(0,4).toInt()
    val m = ym.substring(5,7).toInt()

    val canCreateMonthlyPlaylist by remember {
        Database.playlistTable
                .findByName( "$MONTHLY_PREFIX$ym")
                .map { it == null }
    }.collectAsState( false, Dispatchers.IO )

    Timber.d("CheckMonthlyPlaylist $canCreateMonthlyPlaylist")

        if (canCreateMonthlyPlaylist) {
            val songsMostPlayed by remember {
                val startDate = LocalDate.of(y, m, 1 )
                val endDate = startDate.plusMonths( 1 ).minusDays( 1 )

                Database.eventTable
                        .findSongsMostPlayedBetween(
                            from = TimeDateUtils.toStartDateMillis( startDate ),
                            to = TimeDateUtils.toStartDateMillis( endDate )
                        )
                        .distinctUntilChanged()
            }.collectAsState( emptyList(), Dispatchers.IO )

            Timber.d("SongsMostPlayed ${songsMostPlayed.size}")

            Database.asyncTransaction {
                mapIgnore(
                    playlist = Playlist(name = "${MONTHLY_PREFIX}${ym}"),
                    songs = songsMostPlayed.toTypedArray()
                )
            }
        }
}

@Composable
fun getTitleMonthlyPlaylist(playlist: String): String {

    val y = playlist.substring(0,4)
    val m = playlist.substring(5,7).toInt()
    return when (m) {
        1 -> stringResource(R.string.month_january_s).format(y)
        2 -> stringResource(R.string.month_february_s).format(y)
        3 -> stringResource(R.string.month_march_s).format(y)
        4 -> stringResource(R.string.month_april_s).format(y)
        5 -> stringResource(R.string.month_may_s).format(y)
        6 -> stringResource(R.string.month_june_s).format(y)
        7 -> stringResource(R.string.month_july_s).format(y)
        8 -> stringResource(R.string.month_august_s).format(y)
        9 -> stringResource(R.string.month_september_s).format(y)
        10 -> stringResource(R.string.month_october_s).format(y)
        11 -> stringResource(R.string.month_november_s).format(y)
        12 -> stringResource(R.string.month_december_s).format(y)
        else -> playlist
    }
}

fun getTitleMonthlyPlaylist( context: Context, playlist: String ): String {
    
    fun getMonthString( @DrawableRes monthId: Int, year: String ) =
        context.resources.getString( monthId ).format( year )

    val y = playlist.substring(0,4)
    val m = playlist.substring(5,7).toInt()
    return when( m ) {
        1 -> getMonthString( R.string.month_january_s, y )
        2 -> getMonthString( R.string.month_february_s, y )
        3 -> getMonthString( R.string.month_march_s, y )
        4 -> getMonthString( R.string.month_april_s, y )
        5 -> getMonthString( R.string.month_may_s, y )
        6 -> getMonthString( R.string.month_june_s, y )
        7 -> getMonthString( R.string.month_july_s, y )
        8 -> getMonthString( R.string.month_august_s, y )
        9 -> getMonthString( R.string.month_september_s, y )
        10 -> getMonthString( R.string.month_october_s, y )
        11 -> getMonthString( R.string.month_november_s, y )
        12 -> getMonthString( R.string.month_december_s, y )
        else -> playlist
    }
}


fun getTitleMonthlyPlaylistFromContext(playlist: String, context: Context): String {

    val y = playlist.substring(0,4)
    val m = playlist.substring(5,7).toInt()
    return when (m) {
        1 -> context.resources.getString(R.string.month_january_s).format(y)
        2 -> context.resources.getString(R.string.month_february_s).format(y)
        3 -> context.resources.getString(R.string.month_march_s).format(y)
        4 -> context.resources.getString(R.string.month_april_s).format(y)
        5 -> context.resources.getString(R.string.month_may_s).format(y)
        6 -> context.resources.getString(R.string.month_june_s).format(y)
        7 -> context.resources.getString(R.string.month_july_s).format(y)
        8 -> context.resources.getString(R.string.month_august_s).format(y)
        9 -> context.resources.getString(R.string.month_september_s).format(y)
        10 -> context.resources.getString(R.string.month_october_s).format(y)
        11 -> context.resources.getString(R.string.month_november_s).format(y)
        12 -> context.resources.getString(R.string.month_december_s).format(y)
        else -> playlist
    }
}