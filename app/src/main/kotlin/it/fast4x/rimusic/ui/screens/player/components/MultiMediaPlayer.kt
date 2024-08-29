package it.fast4x.rimusic.ui.screens.player.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import chaintech.videoplayer.model.PlayerConfig
import chaintech.videoplayer.ui.youtube.YouTubePlayerView


@Composable
fun MultiMediaPlayer(
    ytVideoId: String
) {
    YouTubePlayerView(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(3f)
            .padding(10.dp),
        videoId = ytVideoId,
    )
}