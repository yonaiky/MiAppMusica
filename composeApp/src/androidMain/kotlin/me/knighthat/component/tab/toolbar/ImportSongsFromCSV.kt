package me.knighthat.component.tab.toolbar

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.models.Song
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import me.knighthat.component.header.TabToolBar

interface ImportSongsFromCSV: Button {

    companion object {

        fun openFile(
            uri: Uri,
            context: Context ,
            beforeTransaction: (Int, Map<String, String>) -> Unit = { _,_ -> },
            afterTransaction: ( Int, Song ) -> Unit = { _,_ -> }
        ) {
            context.applicationContext
                   .contentResolver
                   .openInputStream(uri)
                   ?.use { inputStream ->
                        csvReader().open(inputStream) {
                            readAllWithHeaderAsSequence().forEachIndexed { index, row: Map<String, String> ->
                                println("mediaItem index song $index")

                                transaction {
                                    beforeTransaction( index, row )
                                    /**/
                                    val mediaId = row["MediaId"]
                                    val title = row["Title"]

                                    if( mediaId == null || title == null)
                                        return@transaction

                                    val song = Song (
                                        id = mediaId,
                                        title = title,
                                        artistsText = row["Artists"],
                                        durationText = row["Duration"],
                                        thumbnailUrl = row["ThumbnailUrl"],
                                        totalPlayTimeMs = 1L
                                    )
                                    afterTransaction( index, song )
                                }
                            }
                        }
                   }
        }
    }

    val context: Context
    val iconId: Int
        @DrawableRes
        get() = R.drawable.resource_import
    val messageId: Int
        @StringRes
        get() = R.string.import_playlist

    /**
     * What happens when user taps on icon.
     */
    fun onShortClick()

    /**
     * What happens when user holds this icon for a while.
     * <p>
     * By default, this will send out message
     */
    /**
     * What happens when user holds this icon for a while.
     * <p>
     * By default, this will send out message
     */
    fun onLongClick() =
        SmartMessage(
            context.resources.getString( messageId ),
            context = context
        )

    @Composable
    override fun ToolBarButton() {
        TabToolBar.Icon(
            iconId =  this.iconId,
            onShortClick = {
                try {
                    onShortClick()
                } catch (_: ActivityNotFoundException) {
                    SmartMessage(
                        context.resources.getString(R.string.info_not_find_app_open_doc),
                        type = PopupType.Warning, context = context
                    )
                }
            },
            onLongClick = ::onLongClick
        )
    }
}