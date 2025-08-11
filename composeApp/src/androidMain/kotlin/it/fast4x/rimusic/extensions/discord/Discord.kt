package it.fast4x.rimusic.extensions.discord

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
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
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import app.kreate.android.R
import com.my.kizzyrpc.KizzyRPC
import com.my.kizzyrpc.model.Activity
import com.my.kizzyrpc.model.Assets
import com.my.kizzyrpc.model.Timestamps
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import it.fast4x.innertube.Innertube
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.cleanPrefix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonArray
import me.knighthat.innertube.Constants
import me.knighthat.utils.ImageProcessor
import me.knighthat.utils.Repository
import me.knighthat.utils.Toaster
import org.jetbrains.annotations.Contract

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun DiscordLoginAndGetToken( onDone: () -> Unit ) {
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
                    fun onRetrieveToken( token: String ) {
                        Preferences.DISCORD_ACCESS_TOKEN.value = token
                        onDone()
                    }

                    @JavascriptInterface
                    @Suppress("unused")     // To stop IDE from complaining
                    fun onFailure( message: String ) {
                        if ( message == "null" )
                            Toaster.e( R.string.error_failed_to_extract_discord_acess_token )
                        else
                            Toaster.e( message )

                        onDone()
                    }

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
                                            
                                            token = i.contentWindow.localStorage.token;
                                        }
                                        
                                        if (token) {
                                            Android.onRetrieveToken(token.slice(1, -1));
                                        } else {
                                            Android.onFailure("null");
                                        }
                                    } catch (err) {
                                        Android.onFailure(err.message);
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

                webView = this
                loadUrl( "https://discord.com/login" )
            }
        }
    )

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}

//<editor-fold desc="Awesome adaptation to make song's thumbnail to show up in RPC. Thanks to NEVARLeVrai">
// https://github.com/NEVARLeVrai/N-Zik
private const val APPLICATION_ID = "1370148610158759966"
private const val TEMP_FILE_HOST = "https://litterbox.catbox.moe/resources/internals/api.php "
private const val MAX_DIMENSION = 1024                           // Per Discord's guidelines
private const val MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024     // 2 MB in bytes

@OptIn(ExperimentalSerializationApi::class)
private suspend fun uploadArtwork( context: Context, artworkUri: Uri? ): Result<Uri> =
    runCatching {
        val uploadableUri = ImageProcessor.compressArtwork(
            context,
            artworkUri,
            MAX_DIMENSION,
            MAX_DIMENSION,
            MAX_FILE_SIZE_BYTES
        )!!
        if( uploadableUri.scheme!!.startsWith( "http" ) )
            return@runCatching uploadableUri

        Innertube.client
                 .submitFormWithBinaryData(
                     url = TEMP_FILE_HOST,
                     formData = formData {
                         val (mimeType, fileData) = with( context.contentResolver ) {
                             getType( uploadableUri )!! to openInputStream( uploadableUri )!!.readBytes()
                         }

                         append("reqtype", "fileupload")
                         append("time", "1h")
                         append("fileToUpload", fileData, Headers.build {
                             append( HttpHeaders.ContentDisposition, "filename=\"${System.currentTimeMillis()}\"" )
                             append( HttpHeaders.ContentType, mimeType )
                         })
                     }
                 )
                 .bodyAsText()
                 .toUri()
    }

private suspend fun getDiscordAssetUri( imageUrl: String, token: String ): String? {
    if ( imageUrl.startsWith( "mp:" ) ) return imageUrl

    return runCatching {
        Innertube.client
                 .post( "https://discord.com/api/v9/applications/$APPLICATION_ID/external-assets" ) {
                     headers.append( "Authorization", token )
                     setBody(
                         // Use this to ensure syntax
                         // {"urls":[imageUrl]}
                         buildJsonObject {
                             putJsonArray( "urls" ) { add( imageUrl ) }
                         }
                     )
                 }
                 .body<JsonArray>()
                 .firstOrNull()
                 ?.jsonObject["external_asset_path"]
                 ?.jsonPrimitive
                 ?.content
                 ?.let { "mp:$it" }
    }.onFailure {
        it.printStackTrace()
        it.message?.also( Toaster::e )
    }.getOrNull()
}

private lateinit var smallImage: String

@Contract("_,null->null")
private suspend fun getLargeImageUrl( context: Context, token: String, artworkUri: Uri? ): String? =
    uploadArtwork( context, artworkUri ).fold(
        onSuccess = {
            getDiscordAssetUri( it.toString(), token )
        },
        onFailure = {
            it.printStackTrace()
            it.message?.also( Toaster::e )

            getSmallImageUrl( token )
        }
    )

private suspend fun getSmallImageUrl( token: String ): String? =
    if ( ::smallImage.isInitialized )
        smallImage
    else
        getDiscordAssetUri("https://i.ibb.co/3mLGkPwY/app-logo.png", token)
            ?.also { smallImage = it }
//</editor-fold>

private lateinit var rpc: KizzyRPC
private lateinit var activity: Activity

@RequiresApi(Build.VERSION_CODES.M)
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
            applicationId = APPLICATION_ID,
            name = BuildConfig.APP_NAME,
            type = TypeDiscordActivity.LISTENING.value,
            buttons = listOf(
                context.getString( R.string.get_app, BuildConfig.APP_NAME ),
                context.getString( R.string.listen_on_youtube )
            )
        )
    }

    rpc.setActivity(
        activity = activity.copy(
            details = cleanPrefix( mediaItem.mediaMetadata.title.toString() ),
            state = cleanPrefix( mediaItem.mediaMetadata.artist.toString() ),
            timestamps = Timestamps(timeEnd, timeStart),
            assets = Assets(
                largeImage = getLargeImageUrl( context, token, mediaItem.mediaMetadata.artworkUri ),
                smallImage = getSmallImageUrl( token ),
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