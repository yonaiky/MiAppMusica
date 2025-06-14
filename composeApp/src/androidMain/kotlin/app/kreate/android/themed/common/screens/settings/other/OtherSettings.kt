package app.kreate.android.themed.common.screens.settings.other

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.net.toUri
import app.kreate.android.R
import app.kreate.android.Settings.DEBUG_LOG
import app.kreate.android.Settings.HOME_SONGS_ON_DEVICE_SHOW_FOLDERS
import app.kreate.android.Settings.IS_PROXY_ENABLED
import app.kreate.android.Settings.KEEP_SCREEN_ON
import app.kreate.android.Settings.LOCAL_SONGS_FOLDER
import app.kreate.android.Settings.PARENTAL_CONTROL
import app.kreate.android.Settings.PLAYER_EXTRA_SPACE
import app.kreate.android.Settings.PROXY_HOST
import app.kreate.android.Settings.PROXY_PORT
import app.kreate.android.Settings.PROXY_SCHEME
import app.kreate.android.themed.common.component.settings.SettingComponents
import app.kreate.android.themed.common.component.settings.SettingEntrySearch
import app.kreate.android.themed.common.component.settings.section
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.screens.settings.ButtonBarSettingEntry
import it.fast4x.rimusic.ui.screens.settings.SettingsEntry
import it.fast4x.rimusic.ui.screens.settings.StringListValueSelectorSettingsEntry
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.isAtLeastAndroid10
import it.fast4x.rimusic.utils.isAtLeastAndroid12
import it.fast4x.rimusic.utils.isAtLeastAndroid6
import it.fast4x.rimusic.utils.isIgnoringBatteryOptimizations
import it.fast4x.rimusic.utils.textCopyToClipboard
import me.knighthat.component.dialog.InputDialogConstraints
import me.knighthat.utils.Toaster
import java.io.File

@Composable
fun OtherSettings() {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()

    val search = remember {
        SettingEntrySearch( scrollState, R.string.tab_miscellaneous, R.drawable.equalizer )
    }
    val paddingValues =
        if( UiType.ViMusic.isCurrent() )
            WindowInsets.statusBars.asPaddingValues()
        else
            PaddingValues()

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

        LazyColumn( state = scrollState ) {
            section( R.string.on_device ) {
                var blackListedPaths by remember {
                    val file = File(context.filesDir, "Blacklisted_paths.txt")
                    if (file.exists()) {
                        mutableStateOf(file.readLines())
                    } else {
                        mutableStateOf(emptyList())
                    }
                }

                StringListValueSelectorSettingsEntry(
                    title = stringResource(R.string.blacklisted_folders),
                    text = stringResource(R.string.edit_blacklist_for_on_device_songs),
                    addTitle = stringResource(R.string.add_folder),
                    addPlaceholder = if (isAtLeastAndroid10) {
                        "Android/media/com.whatsapp/WhatsApp/Media"
                    } else {
                        "/storage/emulated/0/Android/media/com.whatsapp/"
                    },
                    conflictTitle = stringResource(R.string.this_folder_already_exists),
                    removeTitle = stringResource(R.string.are_you_sure_you_want_to_remove_this_folder_from_the_blacklist),
                    context = LocalContext.current,
                    list = blackListedPaths,
                    add = { newPath ->
                        blackListedPaths = blackListedPaths + newPath
                        val file = File(context.filesDir, "Blacklisted_paths.txt")
                        file.writeText(blackListedPaths.joinToString("\n"))
                    },
                    remove = { path ->
                        blackListedPaths = blackListedPaths.filter { it != path }
                        val file = File(context.filesDir, "Blacklisted_paths.txt")
                        file.writeText(blackListedPaths.joinToString("\n"))
                    }
                )

                SettingComponents.BooleanEntry(
                    HOME_SONGS_ON_DEVICE_SHOW_FOLDERS,
                    R.string.folders,
                    R.string.show_folders_in_on_device_page
                )
                AnimatedVisibility( HOME_SONGS_ON_DEVICE_SHOW_FOLDERS.value ) {
                    SettingComponents.InputDialogEntry(
                        LOCAL_SONGS_FOLDER,
                        R.string.folder_that_will_show_when_you_open_on_device_page,
                        InputDialogConstraints.ANDROID_FILE_PATH
                    )
                }
            }
            section( R.string.androidheadunit ) {
                SettingComponents.BooleanEntry(
                    PLAYER_EXTRA_SPACE,
                    R.string.extra_space
                )
            }
            section( R.string.service_lifetime ) {
                SettingComponents.BooleanEntry(
                    KEEP_SCREEN_ON,
                    R.string.keep_screen_on,
                    R.string.prevents_screen_timeout
                )

                SettingComponents.Description( R.string.battery_optimizations_applied, isImportant = true )

                if (isAtLeastAndroid12)
                    SettingComponents.Description( R.string.is_android12 )

                var isIgnoringBatteryOptimizations by remember {
                    mutableStateOf(context.isIgnoringBatteryOptimizations)
                }
                val activityResultLauncher =
                    rememberLauncherForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) {
                        isIgnoringBatteryOptimizations = context.isIgnoringBatteryOptimizations
                    }

                SettingsEntry(
                    title = stringResource(R.string.ignore_battery_optimizations),
                    isEnabled = !isIgnoringBatteryOptimizations,
                    text = if (isIgnoringBatteryOptimizations) {
                        stringResource(R.string.already_unrestricted)
                    } else {
                        stringResource(R.string.disable_background_restrictions)
                    },
                    onClick = {
                        if (!isAtLeastAndroid6) return@SettingsEntry

                        try {
                            activityResultLauncher.launch(
                                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                    data = "package:${context.packageName}".toUri()
                                }
                            )
                        } catch (e: ActivityNotFoundException) {
                            try {
                                activityResultLauncher.launch(
                                    Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                )
                            } catch (e: ActivityNotFoundException) {
                                Toaster.i( R.string.not_find_battery_optimization_settings )
                            }
                        }
                    }
                )
            }
            section( R.string.proxy, R.string.restarting_rimusic_is_required ) {
                SettingComponents.BooleanEntry(
                    IS_PROXY_ENABLED,
                    R.string.enable_proxy
                )

                AnimatedVisibility( IS_PROXY_ENABLED.value ) {
                    Column {
                        SettingComponents.EnumEntry(
                            PROXY_SCHEME,
                            R.string.proxy_mode,
                            { it.name }
                        )

                        SettingComponents.InputDialogEntry(
                            PROXY_HOST,
                            R.string.proxy_host,
                            InputDialogConstraints.URL,
                            keyboardOption = KeyboardOptions(keyboardType = KeyboardType.Uri)
                        )
                        SettingComponents.InputDialogEntry(
                            PROXY_PORT,
                            R.string.proxy_port,
                            constraint = InputDialogConstraints.ONLY_INTEGERS,
                            keyboardOption = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }
            section( R.string.parental_control ) {
                SettingComponents.BooleanEntry(
                    PARENTAL_CONTROL,
                    R.string.parental_control,
                    R.string.info_prevent_play_songs_with_age_limitation
                )
            }
            section( R.string.debug, R.string.restarting_rimusic_is_required ) {
                SettingComponents.BooleanEntry(
                    DEBUG_LOG,
                    R.string.enable_log_debug,
                    R.string.if_enabled_create_a_log_file_to_highlight_errors
                ) {
                    if ( !it ) {
                        val file = File(context.filesDir.resolve("logs"), "RiMusic_log.txt")
                        if (file.exists())
                            file.delete()

                        val filec = File(context.filesDir.resolve("logs"), "RiMusic_crash_log.txt")
                        if (filec.exists())
                            filec.delete()
                    } else
                        Toaster.i( R.string.restarting_rimusic_is_required )
                }

                var text by remember { mutableStateOf(null as String?) }
                ButtonBarSettingEntry(
                    isEnabled = DEBUG_LOG.value,
                    title = stringResource(R.string.copy_log_to_clipboard),
                    text = "",
                    icon = R.drawable.copy,
                    onClick = {
                        val file = File(context.filesDir.resolve("logs"), "RiMusic_log.txt")
                        if (file.exists()) {
                            text = file.readText()
                            text?.let {
                                textCopyToClipboard(it, context)
                            }
                        } else
                            Toaster.w( R.string.no_log_available )
                    }
                )
                ButtonBarSettingEntry(
                    isEnabled = DEBUG_LOG.value,
                    title = stringResource(R.string.copy_crash_log_to_clipboard),
                    text = "",
                    icon = R.drawable.copy,
                    onClick = {
                        val file = File(context.filesDir.resolve("logs"), "RiMusic_crash_log.txt")
                        if (file.exists()) {
                            text = file.readText()
                            text?.let {
                                textCopyToClipboard(it, context)
                            }
                        } else
                            Toaster.w( R.string.no_log_available )
                    }
                )
            }
        }
    }
}