package app.kreate.android.themed.common.component.settings

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.fast4x.rimusic.appContext

fun LazyListScope.entry(
    search: SettingEntrySearch,
    title: String,
    key: Any? = title,
    additionalCheck: Boolean = true,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) {
    if( additionalCheck && search appearsIn title )
        item( key, contentType ) {
            content.invoke( this )
        }
}

fun LazyListScope.entry(
    search: SettingEntrySearch,
    @StringRes titleId: Int,
    key: Any? = titleId,
    additionalCheck: Boolean = true,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) =
    entry( search, appContext().getString( titleId ), key, additionalCheck, contentType, content )

fun LazyListScope.animatedEntry(
    key: Any?,
    visible: Boolean,
    modifier: Modifier = Modifier,
    contentType: Any? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) =
    item( key, contentType ) {
        AnimatedVisibility( visible, modifier, content = content )
    }
