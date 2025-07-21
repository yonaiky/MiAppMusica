package it.fast4x.rimusic.extensions.discord

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import app.kreate.android.R
import com.my.kizzyrpc.KizzyRPC
import com.my.kizzyrpc.model.Activity
import com.my.kizzyrpc.model.Assets
import com.my.kizzyrpc.model.Timestamps
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.cleanPrefix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.knighthat.innertube.Constants
import me.knighthat.utils.Repository
import me.knighthat.utils.Toaster

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DiscordLoginAndGetToken( onDone: () -> Unit ) {
    fun setToken( token: String ) {
        if( token == "null" || token == "error" ) {
            Toaster.e( R.string.error_failed_to_extract_discord_acess_token )
            return
        }

        Preferences.DISCORD_ACCESS_TOKEN.value = token
        onDone()
    }


    var webView: WebView? = null

    // This section is ripped from Metrolist - Full credit to their team
    // Small changes were made in order to make it work with Kreate
    // https://github.com/mostafaalagamy/Metrolist/blob/main/app/src/main/kotlin/com/metrolist/music/ui/screens/settings/DiscordLoginScreen.kt
    AndroidView(
        modifier = Modifier.windowInsetsPadding( LocalPlayerAwareWindowInsets.current )
                           .fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                WebView.setWebContentsDebuggingEnabled(true)

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true

                CookieManager.getInstance().apply {
                    removeAllCookies(null)
                    flush()
                }

                WebStorage.getInstance().deleteAllData()

                addJavascriptInterface(object {
                    @JavascriptInterface
                    @Suppress("unused")     // To stop IDE from complaining
                    fun onRetrieveToken( token: String ) = setToken( token )
                }, "Android")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        if ( url.contains("/channels/@me") || url.contains("/app") ) {
                            view.evaluateJavascript(
                                """
                                (function() {
                                    try {
                                        var token = localStorage.getItem("token");
                                        if (token) {
                                            Android.onRetrieveToken(token.slice(1, -1));
                                        } else {
                                            var i = document.createElement('iframe');
                                            document.body.appendChild(i);
                                            setTimeout(function() {
                                                try {
                                                    var alt = i.contentWindow.localStorage.token;
                                                    if (alt) {
                                                        alert(alt.slice(1, -1));
                                                    } else {
                                                        alert("null");
                                                    }
                                                } catch (e) {
                                                    alert("error");
                                                }
                                            }, 1000);
                                        }
                                    } catch (e) {
                                        alert("error");
                                    }
                                })();
                                """.trimIndent(), null
                            )
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean = false
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onJsAlert(
                        view: WebView,
                        url: String,
                        message: String,
                        result: JsResult
                    ): Boolean {
                        setToken( message )
                        result.confirm()
                        return true
                    }
                }

                webView = this
                loadUrl( "https://discord.com/login" )
            }
        }
    )

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}

private lateinit var rpc: KizzyRPC
private lateinit var activity: Activity

@RequiresApi(Build.VERSION_CODES.N)
fun updateDiscordPresence(
    context: Context,
    mediaItem: MediaItem,
    timeStart: Long,
    timeEnd: Long
) = CoroutineScope(Dispatchers.IO ).launch {
    val token by Preferences.DISCORD_ACCESS_TOKEN
    if( token.isBlank() ) return@launch

    // Check if tokens are different in case
    // user switches between their accounts.
    if( !::rpc.isInitialized || rpc.token != token ) {
        rpc = KizzyRPC( token )
        activity = Activity(
            applicationId = "1370148610158759966",
            name = BuildConfig.APP_NAME,
            type = TypeDiscordActivity.LISTENING.value,
            buttons = listOf(
                appContext().getString( R.string.get_app, BuildConfig.APP_NAME ),
                appContext().getString( R.string.listen_on_youtube )
            ),
        )
    }

    rpc.setActivity(
        activity = activity.copy(
            details = cleanPrefix( mediaItem.mediaMetadata.title.toString() ),
            state = cleanPrefix( mediaItem.mediaMetadata.artist.toString() ),
            timestamps = Timestamps(timeEnd, timeStart),
            assets = Assets(
                largeImage = mediaItem.mediaMetadata.artworkUri.toString(),
                smallImage = "https://i.ibb.co/0yd8QzkV/app-logo.png",
            ),
            metadata = com.my.kizzyrpc.model.Metadata(
                listOf(
                    // https://github.com/knighthat/Kreate/releases/latest
                    "%s/%s".format( Repository.GITHUB, Repository.LATEST_TAG_URL ),
                    // https://music.youtube.com/watch?v={mediaId}
                    "%s/watch?v=%s".format( Constants.YOUTUBE_MUSIC_URL, mediaItem.mediaId ),
                )
            ),
        ),
        status = "online",
        since = System.currentTimeMillis()
    )
}

enum class TypeDiscordActivity (val value: Int) {
    PLAYING(0),
    STREAMING(1),
    LISTENING(2),
    WATCHING(3),
    COMPETING(5)
}