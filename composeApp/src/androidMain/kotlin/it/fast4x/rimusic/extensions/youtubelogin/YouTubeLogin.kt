package it.fast4x.rimusic.extensions.youtubelogin

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.kreate.android.Preferences
import app.kreate.android.R
import it.fast4x.innertube.Innertube
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.knighthat.utils.Toaster

@OptIn(
    DelicateCoroutinesApi::class,
    ExperimentalMaterial3Api::class
)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeLogin( onDone: () -> Unit ) {
    var webView: WebView? = null

    // This section is ripped from Metrolist - Full credit to their team
    // Small changes were made in order to make it work with Kreate
    // https://github.com/mostafaalagamy/Metrolist/blob/main/app/src/main/kotlin/com/metrolist/music/ui/screens/LoginScreen.kt
    AndroidView(
        modifier = Modifier.windowInsetsPadding( LocalPlayerAwareWindowInsets.current )
                           .fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished( view: WebView, url: String? ) {
                        loadUrl("javascript:Android.onRetrieveVisitorData(window.yt.config_.VISITOR_DATA)")
                        loadUrl("javascript:Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID)")

                        if ( url?.startsWith("https://music.youtube.com") == true ) {
                            Preferences.YOUTUBE_COOKIES.value = CookieManager.getInstance().getCookie( url )

                            cookie = CookieManager.getInstance().getCookie( url )
                            GlobalScope.launch {
                                Innertube.accountInfo().onSuccess {
                                    accountName = it?.name.orEmpty()
                                    accountEmail = it?.email.orEmpty()
                                    accountChannelHandle = it?.channelHandle.orEmpty()
                                    accountThumbnail = it?.thumbnailUrl.orEmpty()
                                    onLogin(cookie)
                                }.onFailure {
                                    it.printStackTrace()
                                    it.message?.let( Toaster::e )
                                }
                            }

                            onDone()
                        }
                    }
                }
                settings.apply {
                    javaScriptEnabled = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                }
                addJavascriptInterface(
                    object {
                        @Suppress("unused")     // Stop IDE from complaining & prevent accidental deletion
                        @JavascriptInterface
                        fun onRetrieveVisitorData( newVisitorData: String? ) {
                            Preferences.YOUTUBE_VISITOR_DATA.value = newVisitorData.orEmpty()
                        }

                        @Suppress("unused")     // Stop IDE from complaining & prevent accidental deletion
                        @JavascriptInterface
                        fun onRetrieveDataSyncId( newDataSyncId: String? ) {
                            Preferences.YOUTUBE_SYNC_ID.value = newDataSyncId.orEmpty().substringBefore("||")
                        }
                    },
                    "Android"
                )
                webView = this
                loadUrl("https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com")
            }
        }
    )

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}

