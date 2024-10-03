package me.knighthat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import it.fast4x.rimusic.ui.styling.LocalAppearance

@Composable
fun typography() = LocalAppearance.current.typography

@Composable
@ReadOnlyComposable
fun colorPalette() = LocalAppearance.current.colorPalette

@Composable
fun thumbnailShape() = LocalAppearance.current.thumbnailShape