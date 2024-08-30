package it.fast4x.rimusic.ui.screens.player.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.styling.LocalAppearance
import it.fast4x.rimusic.utils.lastVideoIdKey
import it.fast4x.rimusic.utils.lastVideoSecondsKey
import it.fast4x.rimusic.utils.rememberPreference


@Composable
fun YoutubePlayer(
    ytVideoId: String,
    lifecycleOwner: LifecycleOwner,
    showPlayer: Boolean = true,
    onCurrentSecond: (second: Float) -> Unit
) {

    if (!showPlayer) return

    var lastYTVideoId by rememberPreference(key = lastVideoIdKey, defaultValue = "")
    var lastYTVideoSeconds by rememberPreference(key = lastVideoSecondsKey, defaultValue = 0f)

//    val currentYTVideoId by remember { mutableStateOf(ytVideoId) }
//    println("mediaItem youtubePlayer called currentYTVideoId $currentYTVideoId ytVideoId $ytVideoId lastYTVideoId $lastYTVideoId")

    if (ytVideoId != lastYTVideoId) lastYTVideoSeconds = 0f

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            //.padding(8.dp)
            //.clip(RoundedCornerShape(10.dp))
            .zIndex(2f),
        factory = {
            val iFramePlayerOptions = IFramePlayerOptions.Builder()
                .controls(1)
                .build()

            val listener = object: AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    //println("mediaItem youtubePlayer onReady called lastYTVideoSeconds $lastYTVideoSeconds")
                    youTubePlayer.loadVideo(ytVideoId, lastYTVideoSeconds)
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    onCurrentSecond(second)
                    lastYTVideoSeconds = second
                    lastYTVideoId = ytVideoId
                }

            }


            YouTubePlayerView(context = it).apply {
                enableAutomaticInitialization = false

                lifecycleOwner.lifecycle.addObserver(this)
                /*
                addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        //println("mediaItem youtubePlayer onReady called lastYTVideoSeconds $lastYTVideoSeconds")
                        youTubePlayer.loadVideo(ytVideoId, lastYTVideoSeconds)
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        super.onCurrentSecond(youTubePlayer, second)
                        onCurrentSecond(second)
                        lastYTVideoSeconds = second
                        lastYTVideoId = ytVideoId
                    }
                })
                 */
                initialize(listener, true, iFramePlayerOptions)
            }
        }
    )

}

/*
@Composable
fun YoutubeFullScreenPlayer(
    ytVideoId: String,
    lifecycleOwner: LifecycleOwner
) {
    var isFullscreen by remember { mutableStateOf(false) }
    var fullScreenViewContainer: View? = null

    if (isFullscreen) {
        Box(modifier = Modifier.fillMaxSize()){
            AndroidView(
                modifier = Modifier.fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .zIndex(2f),
                factory = {
                    fullScreenViewContainer!!
                }
            )
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .zIndex(2f),
        factory = {
            val view = LayoutInflater.from(it).inflate(R.layout.activity_fullscreen, null, true)
            val youTubePlayerView = view.findViewById<YouTubePlayerView>(R.id.youtube_player_view)
            //val fullscreenViewContainer = view.findViewById<FrameLayout>(R.id.full_screen_view_container)
            val iFramePlayerOptions = IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1) // enable full screen button
                .build()

            youTubePlayerView.enableAutomaticInitialization = false
            lifecycleOwner.lifecycle.addObserver(youTubePlayerView)

            youTubePlayerView.addFullscreenListener(object : FullscreenListener {
                override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                    fullScreenViewContainer = fullscreenView
                    isFullscreen = true


                    // the video will continue playing in fullscreenView
                    //youTubePlayerView.visibility = View.GONE
                    //fullscreenViewContainer.visibility = View.VISIBLE
                    //fullscreenViewContainer.addView(fullscreenView)

                    // optionally request landscape orientation
                    // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    println("youtubePlayer onEnterFullscreen called")
                }
                override fun onExitFullscreen() {
                    isFullscreen = false

                    // the video will continue playing in the player
                    youTubePlayerView.visibility = View.VISIBLE
                    //fullscreenViewContainer.visibility = View.GONE
                    //fullscreenViewContainer.removeAllViews()
                }
            })

            youTubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo("S0Q4gqBUs7c", 0f)

                    val enterFullscreenButton = view.findViewById<Button>(R.id.enter_fullscreen_button)
                    enterFullscreenButton.setOnClickListener {
                        youTubePlayer.toggleFullscreen()
                    }
                }
            }, iFramePlayerOptions)

            if (youTubePlayerView.parent != null) {
                (youTubePlayerView.parent as ViewGroup).removeView(youTubePlayerView)
            }
            youTubePlayerView
        },
        update = {
            if (isFullscreen) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
            }
        }

    )
}
 */
