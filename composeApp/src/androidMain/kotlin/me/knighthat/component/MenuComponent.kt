package me.knighthat.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import me.knighthat.appContext
import me.knighthat.component.tab.toolbar.Descriptive
import me.knighthat.component.tab.toolbar.MenuIcon

@SuppressLint("ComposableNaming")
@Composable
fun ItemSelector(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.checked
    override val messageId: Int = R.string.item_select
    override val menuIconTitle: String
        @Composable
        get() = "${stringResource(R.string.item_select)}/${stringResource(R.string.item_deselect)}"

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun PlayNext(
    onClick: () -> Unit
): MenuIcon = object: MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.play_skip_forward
    override val messageId: Int = R.string.play_next
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun Enqueue(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.enqueue
    override val messageId: Int = R.string.enqueue
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun AddToFavorite(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.heart
    override val messageId: Int = R.string.add_to_favorites
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun Synchronize(
    onClick: () -> Unit
): MenuIcon = object: MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.sync
    override val messageId: Int = R.string.sync
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
        SmartMessage(
            appContext().resources.getString( R.string.done ),
            context = appContext()
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun ListenOnYouTube(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.play
    override val messageId: Int = R.string.listen_on_youtube
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun ThumbnailPicker(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.image
    override val messageId: Int = R.string.edit_thumbnail
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun ResetThumbnail(
    onClick: () -> Unit
): MenuIcon = object : MenuIcon, Descriptive {

    val menuState: MenuState = LocalMenuState.current
    override val iconId: Int = R.drawable.image
    override val messageId: Int = R.string.edit_thumbnail
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        onClick()
        menuState.hide()
    }
}