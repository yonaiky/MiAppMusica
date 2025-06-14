package it.fast4x.rimusic.ui.screens.settings

import android.annotation.SuppressLint
import android.text.format.Formatter
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import app.kreate.android.BuildConfig
import app.kreate.android.R
import app.kreate.android.Settings
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingHeader
import coil.annotation.ExperimentalCoilApi
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
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.components.themed.InputNumericDialog
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.RestartPlayerService
import it.fast4x.rimusic.utils.asMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.knighthat.coil.ImageCacheFactory
import me.knighthat.component.export.ExportDatabaseDialog
import me.knighthat.component.export.ExportSettingsDialog
import me.knighthat.component.import.ImportDatabase
import me.knighthat.component.import.ImportMigration
import me.knighthat.component.import.ImportSettings
import me.knighthat.utils.Toaster

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalCoilApi::class)
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun DataSettings() {
    val context = LocalContext.current
    val binder = LocalPlayerServiceBinder.current

    var showExoPlayerCustomCacheDialog by remember { mutableStateOf(false) }
    var exoPlayerCustomCache by Settings.SONG_CACHE_CUSTOM_SIZE

    var showCoilCustomDiskCacheDialog by remember { mutableStateOf(false) }
    var coilCustomDiskCache by Settings.THUMBNAIL_CACHE_CUSTOM_SIZE


    var cleanCacheOfflineSongs by remember {
        mutableStateOf(false)
    }

    var cleanDownloadCache by remember {
        mutableStateOf(false)
    }
    var cleanCacheImages by remember {
        mutableStateOf(false)
    }

    if (cleanCacheOfflineSongs) {
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
    }

    if (cleanDownloadCache) {
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
    }

    if (cleanCacheImages) {
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_delete_cache),
                           onDismiss = {
                               cleanCacheImages = false
                           },
                           onConfirm = ImageCacheFactory.DISK_CACHE::clear
        )
    }

    var restartService by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(colorPalette().background0)
            //.fillMaxSize()
        .fillMaxHeight()
            .fillMaxWidth(
                if (NavigationBarPosition.Right.isCurrent())
                    Dimensions.contentWidthRightBar
                else
                    1f
            )
            .verticalScroll(rememberScrollState())
        /*
         *            .padding(
         *                LocalPlayerAwareWindowInsets.current
         *                    .only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
         *                    .asPaddingValues()
    )

    */
    ) {
        HeaderWithIcon(
            title = stringResource(R.string.tab_data),
                       iconId = R.drawable.server,
                       enabled = false,
                       showIcon = true,
                       modifier = Modifier,
                       onClick = {}
        )

        SettingComponents.Description( R.string.cache_cleared )

        SettingHeader( R.string.cache )

        ImageCacheFactory.DISK_CACHE.size.let { diskCacheSize ->
            val coilDiskCacheMaxSize by Settings.THUMBNAIL_CACHE_SIZE

            val subtitle by remember { derivedStateOf {
                // How much space taken by this cache
                val diskUsage = Formatter.formatShortFileSize( context, diskCacheSize )
                // Total space can be had
                val maxSize = if( coilDiskCacheMaxSize == CoilDiskCacheMaxSize.Custom ) {
                    val sizeBytes = coilCustomDiskCache * 1000L * 1000
                    val formattedSize = Formatter.formatShortFileSize( context, sizeBytes )
                    "/$formattedSize"
                } else
                    ""
                // Percentage based on used/total
                val total = when( coilDiskCacheMaxSize ) {
                    CoilDiskCacheMaxSize.Custom -> coilCustomDiskCache * 1000L * 1000
                    else -> coilDiskCacheMaxSize.bytes
                }
                val result = (diskCacheSize * 100) / total
                val percentage = "($result%)"

                val used = context.getString( R.string.used )
                " - $diskUsage$maxSize$used $percentage"
            }}
            SettingComponents.EnumEntry(
                Settings.THUMBNAIL_CACHE_SIZE,
                R.string.image_cache_max_size,
                subtitle = Settings.THUMBNAIL_CACHE_SIZE.value.text + subtitle,
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

                restartService = true
            }
            RestartPlayerService(restartService, onRestart = { restartService = false } )

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
                        restartService = true
                    }
                )
                RestartPlayerService(restartService, onRestart = { restartService = false } )
            }

            CacheSpaceIndicator(cacheType = CacheType.Images, horizontalPadding = 20.dp)
        }

        binder?.cache?.cacheSpace?.let { diskCacheSize ->
            val exoPlayerDiskCacheMaxSize by Settings.SONG_CACHE_SIZE

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
                Settings.SONG_CACHE_SIZE,
                R.string.song_cache_max_size,
                subtitle = Settings.SONG_CACHE_SIZE.value.text + subtitle,
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

                restartService = true
            }

            RestartPlayerService(restartService, onRestart = { restartService = false } )

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
                        restartService = true
                    }
                )
                RestartPlayerService(restartService, onRestart = { restartService = false } )
            }

            CacheSpaceIndicator(cacheType = CacheType.CachedSongs, horizontalPadding = 20.dp)
        }

        binder?.downloadCache?.cacheSpace?.let { diskCacheSize ->
            val exoPlayerDiskDownloadCacheMaxSize by Settings.SONG_DOWNLOAD_SIZE

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
                Settings.SONG_DOWNLOAD_SIZE,
                R.string.song_download_max_size,
                subtitle = Settings.SONG_DOWNLOAD_SIZE.value.text + subtitle,
                action = SettingComponents.Action.RESTART_PLAYER_SERVICE,
                trailingContent = {
                    HeaderIconButton(
                        icon = R.drawable.trash,
                        enabled = true,
                        color = colorPalette().text,
                        onClick = { cleanDownloadCache = true }
                    )
                }
            ) { restartService = true }

            RestartPlayerService(restartService, onRestart = { restartService = false } )

            CacheSpaceIndicator(cacheType = CacheType.DownloadedSongs, horizontalPadding = 20.dp)
        }

        SettingComponents.EnumEntry(
            Settings.EXO_CACHE_LOCATION,
            R.string.set_cache_location,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ){ restartService = true }

        SettingComponents.Description( R.string.info_private_cache_location_can_t_cleaned )
        RestartPlayerService(restartService, onRestart = { restartService = false } )

        SettingHeader( R.string.title_backup_and_restore )

        val exportDbDialog = ExportDatabaseDialog( context )
        exportDbDialog.Render()

        SettingsEntry(
            title = stringResource( R.string.save_to_backup ),
            text = stringResource( R.string.export_the_database ),
            onClick = exportDbDialog::showDialog
        )
        SettingComponents.Description( R.string.personal_preference )

        val importDatabase = ImportDatabase( context )

        SettingsEntry(
            title = stringResource(R.string.restore_from_backup),
            text = stringResource(R.string.import_the_database),
            onClick = importDatabase::onShortClick
        )
        SettingComponents.Description(
            stringResource( R.string.existing_data_will_be_overwritten, BuildConfig.APP_NAME ),
            isImportant = true
        )

        val exportSettingsDialog = ExportSettingsDialog( context )
        exportSettingsDialog.Render()

        SettingsEntry(
            title = exportSettingsDialog.dialogTitle,
            text = stringResource( R.string.store_settings_in_a_file ),
            onClick = exportSettingsDialog::showDialog
        )
        SettingComponents.Description(
            R.string.description_exclude_credentials,
            isImportant = true
        )

        val importSettings = ImportSettings( context )

        SettingsEntry(
            title = stringResource( R.string.title_import_settings ),
            text = stringResource( R.string.restore_settings_from_file, stringResource( R.string.title_export_settings ) ),
            onClick = importSettings::onShortClick
        )
        SettingComponents.Description(
            stringResource( R.string.existing_data_will_be_overwritten, BuildConfig.APP_NAME ),
            isImportant = true
        )

        val importMigration = ImportMigration( context, binder )

        SettingsEntry(
            title = "Import migration file",
            text = "For old users before conversion. \nUse old app to make a backup for migration",
            onClick = importMigration::onShortClick
        )

        SettingHeader( R.string.search_history )

        SettingComponents.BooleanEntry(
            Settings.PAUSE_SEARCH_HISTORY,
            R.string.pause_search_history,
            R.string.neither_save_new_searched_query,
            action = SettingComponents.Action.RESTART_PLAYER_SERVICE
        ) { restartService = true }
        RestartPlayerService(restartService, onRestart = { restartService = false } )

        val queriesCount by remember {
            Database.searchTable
                    .findAllContain("")
                    .map { it.size }
        }.collectAsState( 0, Dispatchers.IO )

        SettingsEntry(
            title = stringResource(R.string.clear_search_history),
                      text = if (queriesCount > 0) {
                          "${stringResource(R.string.delete)} " + queriesCount + stringResource(R.string.search_queries)
                      } else {
                          stringResource(R.string.history_is_empty)
                      },
                      isEnabled = queriesCount > 0,
                      onClick = {
                          Database.asyncTransaction {
                              searchTable.deleteAll()
                          }

                          Toaster.done()
                      }
        )
        SettingsGroupSpacer(
            modifier = Modifier.height(Dimensions.bottomSpacer)
        )
    }
}
