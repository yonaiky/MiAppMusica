package it.fast4x.rimusic.ui.screens.home


import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.fast4x.compose.persist.persistList
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.ArtistSortBy
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.fast4x.rimusic.ui.components.themed.Header
import it.fast4x.rimusic.ui.components.themed.HeaderIconButton
import it.fast4x.rimusic.ui.components.themed.HeaderInfo
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.components.themed.SortMenu
import it.fast4x.rimusic.ui.items.ArtistItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.utils.UiTypeKey
import it.fast4x.rimusic.utils.artistSortByKey
import it.fast4x.rimusic.utils.artistSortOrderKey
import it.fast4x.rimusic.utils.contentWidthKey
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.preferences
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.showSearchTabKey

@ExperimentalMaterialApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun HomeArtistList(
    onArtistClick: (Artist) -> Unit,
    onSearchClick: () -> Unit,
) {
    val (colorPalette, typography) = LocalAppearance.current
    val menuState = LocalMenuState.current
    var sortBy by rememberPreference(artistSortByKey, ArtistSortBy.DateAdded)
    var sortOrder by rememberPreference(artistSortOrderKey, SortOrder.Descending)
    val uiType  by rememberPreference(UiTypeKey, UiType.RiMusic)

    var items by persistList<Artist>("home/artists")

    LaunchedEffect(sortBy, sortOrder) {
        Database.artists(sortBy, sortOrder).collect { items = it }
    }


    val thumbnailSizeDp = Dimensions.thumbnails.song * 2
    val thumbnailSizePx = thumbnailSizeDp.px

    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (sortOrder == SortOrder.Ascending) 0f else 180f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing), label = ""
    )

    /*
    var showSortTypeSelectDialog by remember {
        mutableStateOf(false)
    }
     */

    val lazyGridState = rememberLazyGridState()

    val context = LocalContext.current
    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)
    val contentWidth = context.preferences.getFloat(contentWidthKey,0.8f)

    val showSearchTab by rememberPreference(showSearchTabKey, false)
    //val effectRotationEnabled by rememberPreference(effectRotationKey, true)
    var isRotated by rememberSaveable { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 360F else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box (
        modifier = Modifier
            .background(colorPalette.background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(if (navigationBarPosition == NavigationBarPosition.Left) 1f else contentWidth)
    ) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(Dimensions.thumbnails.song * 2 + Dimensions.itemsVerticalPadding * 2),
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Vertical + WindowInsetsSides.End).asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(Dimensions.itemsVerticalPadding * 2),
            horizontalArrangement = Arrangement.spacedBy(
                space = Dimensions.itemsVerticalPadding * 2,
                alignment = Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .background(colorPalette.background0)
                .fillMaxSize()
        ) {
            item(
                key = "header",
                contentType = 0,
                span = { GridItemSpan(maxLineSpan) }
            ) {

                HeaderWithIcon(
                    title = stringResource(R.string.artists),
                    iconId = R.drawable.search,
                    enabled = true,
                    showIcon = !showSearchTab,
                    modifier = Modifier,
                    onClick = onSearchClick
                )


                Row (
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                Header(title = "") {
                    HeaderInfo(
                        title = "${items.size}",
                        icon = painterResource(R.drawable.artists),
                        spacer = 0
                    )

                    HeaderIconButton(
                        modifier = Modifier.rotate(rotationAngle),
                        icon = R.drawable.dice,
                        enabled = items.isNotEmpty() ,
                        color = colorPalette.text,
                        onClick = {
                            isRotated = !isRotated
                            onArtistClick(items.get((0..<items.size).random()))
                        },
                        iconSize = 16.dp
                    )

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    BasicText(
                        text = when (sortBy) {
                            ArtistSortBy.Name -> stringResource(R.string.sort_name)
                            ArtistSortBy.DateAdded -> stringResource(R.string.sort_date_added)
                        },
                        style = typography.xs.semiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable {
                                menuState.display{
                                    SortMenu(
                                        title = stringResource(R.string.sorting_order),
                                        onDismiss = menuState::hide,
                                        onName= { sortBy = ArtistSortBy.Name },
                                        onDateAdded = { sortBy = ArtistSortBy.DateAdded },
                                    )
                                }
                                //showSortTypeSelectDialog = true
                            }
                    )
                    /*
                    if (showSortTypeSelectDialog)
                        ValueSelectorDialog(
                            onDismiss = { showSortTypeSelectDialog = false },
                            title = stringResource(R.string.sorting_order),
                            selectedValue = sortBy,
                            values = enumValues<ArtistSortBy>().toList(),
                            onValueSelected = { sortBy = it },
                            valueText = {
                                when (it) {
                                    ArtistSortBy.Name -> stringResource(R.string.sort_name)
                                    ArtistSortBy.DateAdded -> stringResource(R.string.sort_date_added)
                                }
                            }
                        )

                     */
                    /*
                    HeaderIconButton(
                        icon = R.drawable.text,
                        color = if (sortBy == ArtistSortBy.Name) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = ArtistSortBy.Name }
                    )

                    HeaderIconButton(
                        icon = R.drawable.time,
                        color = if (sortBy == ArtistSortBy.DateAdded) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = ArtistSortBy.DateAdded }
                    )

                    Spacer(
                        modifier = Modifier
                            .width(2.dp)
                    )
                     */

                    HeaderIconButton(
                        icon = R.drawable.arrow_up,
                        color = colorPalette.text,
                        onClick = { sortOrder = !sortOrder },
                        modifier = Modifier
                            .graphicsLayer { rotationZ = sortOrderIconRotation }
                    )
                }
            }

            }

            items(items = items, key = Artist::id) { artist ->
                ArtistItem(
                    artist = artist,
                    thumbnailSizePx = thumbnailSizePx,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clickable(onClick = { onArtistClick(artist) })
                        .animateItemPlacement()
                )
            }
        }

        FloatingActionsContainerWithScrollToTop(lazyGridState = lazyGridState)

        //if(uiType == UiType.ViMusic)
        FloatingActionsContainerWithScrollToTop(
            lazyGridState = lazyGridState,
            iconId = R.drawable.search,
            onClick = onSearchClick
        )


    }
}
