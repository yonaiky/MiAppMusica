package app.kreate.android.themed.common.component.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.colorPalette

fun LazyListScope.section(
    @StringRes headerTitleId: Int,
    subtitle: String = "",
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) {
    stickyHeader {
        SettingHeader(
            headerTitleId,
            Modifier.background( colorPalette().background0 ),
            subtitle
        )
    }
    item( key, contentType ) {
        content.invoke( this )
    }
}

fun LazyListScope.section(
    @StringRes headerTitleId: Int,
    @StringRes subtitleId: Int = -1,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) {
    val subtitle = if( subtitleId != -1 ) appContext().getString( subtitleId ) else ""
    section( headerTitleId, subtitle, key, contentType, content )
}

fun LazyListScope.section(
    @StringRes headerTitleId: Int,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) = section( headerTitleId, "", key, contentType, content )