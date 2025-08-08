package app.kreate.android.themed.common.screens.settings

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.coil3.ImageFactory
import app.kreate.android.themed.common.component.settings.RestartPlayerService
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.entry
import app.kreate.android.themed.common.component.settings.header
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.CacheType
import it.fast4x.rimusic.enums.CoilDiskCacheMaxSize
import it.fast4x.rimusic.enums.ExoPlayerDiskCacheMaxSize
import it.fast4x.rimusic.enums.ExoPlayerDiskDownloadCacheMaxSize
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.service.MyDownloadHelper
import it.fast4x.rimusic.ui.components.themed.CacheSpaceIndicator
import it.fast4x.rimusic.ui.components.themed.ConfirmationDialog
import it.fast4x.rimusic.ui.components.themed.HeaderIconButton
import it.fast4x.rimusic.ui.components.themed.InputNumericDialog
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.asMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.knighthat.component.export.ExportDatabaseDialog
import me.knighthat.component.export.ExportSettingsDialog
import me.knighthat.component.import.ImportDatabase
import me.knighthat.component.import.ImportMigration
import me.knighthat.component.import.ImportSettings
import me.knighthat.utils.Toaster

@UnstableApi
@Composable
fun DataSettings( paddingValues: PaddingValues ) {
    val context = LocalContext.current
    val binder = LocalPlayerServiceBinder.current
    val scrollState = rememberLazyListState()

    val search = remember {
        SettingEntrySearch( scrollState, R.string.tab_data, R.drawable.server )
    }
    val (restartService, onRestartServiceChange) = rememberSaveable { mutableStateOf( false ) }
    var showExoPlayerCustomCacheDialog by remember { mutableStateOf(false) }
    var showCoilCustomDiskCacheDialog by remember { mutableStateOf(false) }

    var cleanCacheImages by remember { mutableStateOf( false ) }
    if ( cleanCacheImages ) {
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_delete_cache),
            onDismiss = {
                cleanCacheImages = false
            },
            onConfirm = ImageFactory.diskCache::clear
        )
    }

    var cleanCacheOfflineSongs by remember { mutableStateOf( false ) }
    if ( cleanCacheOfflineSongs )
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_delete_cache),
            onDismiss = {
                cleanCacheOfflineSongs = false
            },
            onConfirm = {
                binder?.cache?.keys?.forEach { song ->
                    binder.cache.removeResource(song)
                }
            }
        )

    var cleanDownloadCache by remember { mutableStateOf( false ) }
    if ( cleanDownloadCache )
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_delete_cache),
            onDismiss = {
                cleanDownloadCache = false
            },
            onConfirm = {
                binder?.downloadCache?.keys?.forEach { songId ->
                    binder.downloadCache.removeResource(songId)

                    CoroutineScope(Dispatchers.IO).launch {
                        Database.songTable
                                .findById( songId )
                                .first()
                                ?.asMediaItem
                                ?.let { MyDownloadHelper.removeDownload( context, it ) }
                    }
                }
            }
        )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background( colorPalette().background0 )
                           .padding( paddingValues )
                           .fillMaxHeight()
                           .fillMaxWidth(
                               if ( NavigationBarPosition.Right.isCurrent() )
                                   Dimensions.contentWidthRightBar
                               else
                                   1f
                           )
    ) {
        search.ToolBarButton()

        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = Dimensions.bottomSpacer)
        ) {
            header(
                titleId = R.string.cache,
                subtitle = { stringResource( R.string.cache_cleared ) }
            )
            entry( search, R.string.image_cache_max_size ) {
                ImageFactory.diskCache.size.let { diskCacheSize ->
                    var coilCustomDiskCache by Preferences.THUMBNAIL_CACHE_CUSTOM_SIZE
                    val coilDiskCacheMaxSize by Preferences.THUMBNAIL_CACHE_SIZE

                    val subtitle by remember { derivedStateOf {
                        // How much space taken by this cache
                        val diskUsage = Formatter.formatShortFileSize( context, diskCacheSize )
                        // Total space can be had
                        val total = when( coilDiskCacheMaxSize ) {
                            CoilDiskCacheMaxSize.Custom -> coilCustomDiskCache * 1000L * 1000
                            else -> coilDiskCacheMaxSize.bytes
                        }
                        val maxSize = if( coilDiskCacheMaxSize == CoilDiskCacheMaxSize.Custom ) {
                            val sizeBytes = total * 1000L * 1000
                            val formattedSize = Formatter.formatShortFileSize( context, sizeBytes )
                            "/$formattedSize"
                        } else
                            ""
                        // Percentage based on used/total
                        val result = (diskCacheSize * 100) / total
                        val percentage = "($result%)"

                        val used = context.getString( R.string.used )
                        " - $diskUsage$maxSize$used $percentage"
                    }}
                    SettingComponents.EnumEntry(
                        Preferences.THUMBNAIL_CACHE_SIZE,
                        R.string.image_cache_max_size,
                        subtitle = Preferences.THUMBNAIL_CACHE_SIZE.value.text + subtitle,
                        action = SettingComponents.Action.RESTART_PLAYER_SERVICE,
                        trailingContent = {
                            HeaderIconButton(
                                icon = R.drawable.trash,
                                enabled = true,
                                color = colorPalette().text,
                                onClick = { cleanCacheImages = true }
                            )
                        }
                    ) {
                        if (coilDiskCacheMaxSize == CoilDiskCacheMaxSize.Custom)
                            showCoilCustomDiskCacheDialog = true

                        RestartPlayerService.requestRestart()
                    }

                    if (showCoilCustomDiskCacheDialog) {
                        InputNumericDialog(
                            title = stringResource(R.string.set_custom_cache),
                            placeholder = stringResource(R.string.enter_value_in_mb),
                            value = coilCustomDiskCache.toString(),
                            valueMin = "32",
                            valueMax = "10000",
                            onDismiss = { showCoilCustomDiskCacheDialog = false },
                            setValue = {
                                //Log.d("customCache", it)
                                coilCustomDiskCache = it.toInt()
                                showCoilCustomDiskCacheDialog = false

                                RestartPlayerService.requestRestart()
                            }
                        )
                    }

                    CacheSpaceIndicator(cacheType = CacheType.Images, horizontalPadding = 20.dp)
                }
            }
            entry( search, R.string.song_cache_max_size ) {
                binder?.cache?.cacheSpace?.let { diskCacheSize ->
                    var exoPlayerCustomCache by Preferences.SONG_CACHE_CUSTOM_SIZE
                    val exoPlayerDiskCacheMaxSize by Preferences.SONG_CACHE_SIZE

                    val subtitle by remember( diskCacheSize ) { derivedStateOf {
                        // How much space taken by this cache
                        val diskUsage = Formatter.formatShortFileSize( context, diskCacheSize )
                        // Total space can be had
                        val maxSize = if( exoPlayerDiskCacheMaxSize == ExoPlayerDiskCacheMaxSize.Custom ) {
                            val sizeBytes = exoPlayerCustomCache * 1000L * 1000
                            val formattedSize = Formatter.formatShortFileSize( context, sizeBytes )
                            "/$formattedSize"
                        } else
                            ""
                        // Percentage based on used/total
                        val percentage = when( exoPlayerDiskCacheMaxSize ) {
                            ExoPlayerDiskCacheMaxSize.Unlimited -> ""
                            else -> {
                                val total = if( exoPlayerDiskCacheMaxSize == ExoPlayerDiskCacheMaxSize.Custom )
                                    exoPlayerCustomCache * 1000L * 1000
                                else
                                    exoPlayerDiskCacheMaxSize.bytes
                                val result = (diskCacheSize * 100) / total
                                "($result%)"
                            }
                        }

                        if( exoPlayerDiskCacheMaxSize != ExoPlayerDiskCacheMaxSize.Disabled ) {
                            val used = context.getString(R.string.used)
                            " - $diskUsage$maxSize$used $percentage"
                        } else
                            ""
                    }}
                    SettingComponents.EnumEntry(
                        Preferences.SONG_CACHE_SIZE,
                        R.string.song_cache_max_size,
                        subtitle = Preferences.SONG_CACHE_SIZE.value.text + subtitle,
                        action = SettingComponents.Action.RESTART_PLAYER_SERVICE,
                        trailingContent = {
                            HeaderIconButton(
                                icon = R.drawable.trash,
                                enabled = true,
                                color = colorPalette().text,
                                onClick = { cleanCacheImages = true }
                            )
                        }
                    ) {
                        if (exoPlayerDiskCacheMaxSize == ExoPlayerDiskCacheMaxSize.Custom)
                            showExoPlayerCustomCacheDialog = true

                        RestartPlayerService.requestRestart()
                    }


                    if (showExoPlayerCustomCacheDialog) {
                        InputNumericDialog(
                            title = stringResource(R.string.set_custom_cache),
                            placeholder = stringResource(R.string.enter_value_in_mb),
                            value = exoPlayerCustomCache.toString(),
                            valueMin = "32",
                            valueMax = "10000",
                            onDismiss = { showExoPlayerCustomCacheDialog = false },
                            setValue = {
                                //Log.d("customCache", it)
                                exoPlayerCustomCache = it.toInt()
                                showExoPlayerCustomCacheDialog = false

                                RestartPlayerService.requestRestart()
                            }
                        )
                    }

                    CacheSpaceIndicator(cacheType = CacheType.CachedSongs, horizontalPadding = 20.dp)
                }
            }
            entry( search, R.string.song_download_max_size ) {
                binder?.downloadCache?.cacheSpace?.let { diskCacheSize ->
                    val exoPlayerDiskDownloadCacheMaxSize by Preferences.SONG_DOWNLOAD_SIZE

                    val subtitle by remember( diskCacheSize ) { derivedStateOf {
                        // How much space taken by this cache
                        val diskUsage = Formatter.formatShortFileSize( context, diskCacheSize )
                        // Percentage based on used/total
                        val percentage = when( exoPlayerDiskDownloadCacheMaxSize ) {
                            ExoPlayerDiskDownloadCacheMaxSize.Unlimited -> ""
                            else -> {
                                val result = (diskCacheSize * 100) / exoPlayerDiskDownloadCacheMaxSize.bytes
                                "($result%)"
                            }
                        }

                        if( exoPlayerDiskDownloadCacheMaxSize != ExoPlayerDiskDownloadCacheMaxSize.Disabled ) {
                            val used = context.getString(R.string.used)
                            " - $diskUsage$used $percentage"
                        } else
                            ""
                    }}
                    SettingComponents.EnumEntry(
                        Preferences.SONG_DOWNLOAD_SIZE,
                        R.string.song_download_max_size,
                        subtitle = Preferences.SONG_DOWNLOAD_SIZE.value.text + subtitle,
                        action = SettingComponents.Action.RESTART_PLAYER_SERVICE,
                        trailingContent = {
                            HeaderIconButton(
                                icon = R.drawable.trash,
                                enabled = true,
                                color = colorPalette().text,
                                onClick = { cleanDownloadCache = true }
                            )
                        }
                    )

                    CacheSpaceIndicator(cacheType = CacheType.DownloadedSongs, horizontalPadding = 20.dp)
                }
            }

            header(
                titleId = R.string.title_backup_and_restore,
                subtitle = { stringResource( R.string.existing_data_will_be_overwritten, BuildConfig.APP_NAME ) }
            )
            entry( search, R.string.save_to_backup ) {
                val exportDbDialog = ExportDatabaseDialog( context )
                exportDbDialog.Render()

                SettingComponents.Text(
                    title = stringResource( R.string.save_to_backup ),
                    subtitle = stringResource( R.string.export_the_database ),
                    onClick = exportDbDialog::showDialog
                )
            }
            entry( search, R.string.restore_from_backup ) {
                val importDatabase = ImportDatabase( context )

                SettingComponents.Text(
                    title = stringResource(R.string.restore_from_backup),
                    subtitle = stringResource(R.string.import_the_database),
                    onClick = importDatabase::onShortClick
                )
            }
            entry( search, R.string.store_settings_in_a_file ) {
                val exportSettingsDialog = ExportSettingsDialog( context )
                exportSettingsDialog.Render()

                SettingComponents.Text(
                    title = exportSettingsDialog.dialogTitle,
                    subtitle = stringResource( R.string.description_exclude_credentials ),
                    onClick = exportSettingsDialog::showDialog
                )
            }
            entry( search, R.string.title_import_settings ) {
                val importSettings = ImportSettings( context )

                SettingComponents.Text(
                    title = stringResource( R.string.title_import_settings ),
                    subtitle = stringResource( R.string.restore_settings_from_file, stringResource( R.string.title_export_settings ) ),
                    onClick = importSettings::onShortClick
                )
            }
            entry( search, "Import migration file" ) {
                val importMigration = ImportMigration( context, binder )

                SettingComponents.Text(
                    title = "Import migration file",
                    subtitle = "For old users before conversion. \nUse old app to make a backup for migration",
                    onClick = importMigration::onShortClick
                )
            }

            header( R.string.search_history )
            entry( search, R.string.pause_search_history ) {
                SettingComponents.BooleanEntry(
                    Preferences.PAUSE_SEARCH_HISTORY,
                    R.string.pause_search_history,
                    R.string.neither_save_new_searched_query,
                    action = SettingComponents.Action.RESTART_PLAYER_SERVICE
                )
            }
            entry( search, R.string.clear_search_history ) {
                val subtitle by remember {
                    Database.searchTable
                            .findAllContain( "" )
                            .map { queries ->

                                if ( queries.isNotEmpty() )
                                    context.getString(
                                        R.string.setting_description_delete_search_history,
                                        context.resources.getQuantityString(
                                            R.plurals.query,
                                            queries.size,
                                            queries.size
                                        )
                                    )
                                else
                                    context.getString( R.string.history_is_empty )
                            }
                }.collectAsState( "", Dispatchers.IO )

                SettingComponents.Text(
                    title = stringResource( R.string.clear_search_history ),
                    subtitle = subtitle,
                    onClick = {
                        Database.asyncTransaction {
                            searchTable.deleteAll()

                            Toaster.done()
                        }
                    }
                )
            }
        }
    }
}