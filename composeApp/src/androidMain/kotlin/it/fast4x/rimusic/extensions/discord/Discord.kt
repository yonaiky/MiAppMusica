package it.fast4x.rimusic.extensions.discord

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.core.graphics.scale
import androidx.core.net.toFile
import androidx.media3.common.MediaItem
import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import app.kreate.android.R
import com.my.kizzyrpc.KizzyRPC
import com.my.kizzyrpc.model.Activity
import com.my.kizzyrpc.model.Assets
import com.my.kizzyrpc.model.Timestamps
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import it.fast4x.innertube.Innertube
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.cleanPrefix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonArray
import me.knighthat.innertube.Constants
import me.knighthat.utils.Repository
import me.knighthat.utils.Toaster
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.Contract
import java.io.File
import java.io.FileOutputStream

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

//<editor-fold desc="Awesome adaptation to make song's thumbnail to show up in RPC. Thanks to NEVARLeVrai">
// https://github.com/NEVARLeVrai/N-Zik
private const val APPLICATION_ID = "1370148610158759966"

private fun compressImage(file: File, maxSize: Int = 512): File? {
    return try {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = false }
        var bitmap = BitmapFactory.decodeFile(file.absolutePath, options)

        if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val ratio = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
            val width = (bitmap.width * ratio).toInt()
            val height = (bitmap.height * ratio).toInt()
            bitmap = bitmap.scale(width, height)
        }

        val compressedFile = File.createTempFile("compressed_", ".jpg", file.parentFile)
        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 5, out)
        }
        compressedFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

const val TMP_FILES_URL = "https://tmpfiles.org/api/v1/upload"

private suspend fun uploadArtwork( artworkUri: Uri ): String? {
    val file = artworkUri.toFile()
    val compressed = if (file.length() > 1_000_000) compressImage(file) else file

    try {
        val body: MultipartBody = MultipartBody.Builder()
                                               .setType(MultipartBody.FORM)
                                               .addFormDataPart(
                                                   name = "file",
                                                   filename = compressed!!.name,
                                                   body = compressed.readBytes()
                                                       .toRequestBody("image/*".toMediaType())
                                               )
                                               .build()
        return Innertube.client
                        .post( TMP_FILES_URL ) {
                            setBody( body )
                        }
                        .body<JsonObject>()["data"]
                        ?.jsonObject["url"]
                        ?.jsonPrimitive
                        ?.content
                        ?.replace(
                            "https://tmpfiles.org/",
                            "https://tmpfiles.org/dl/"
                        )
    } catch (e: Exception) {
        e.printStackTrace()
        e.message?.also( Toaster::e )

        return null
    } finally {
        if (compressed != null && compressed != file) compressed.delete()
    }
}

private fun isLocalArtwork( artworkUri: Uri ): Boolean =
    when( artworkUri.scheme ) {
        "file", "content" -> artworkUri.toFile().exists()
        else              -> false
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
private suspend fun getLargeImageUrl( token: String, artworkUri: Uri? ): String? {
    if( artworkUri == null ) return null

    val isLocalArtwork = isLocalArtwork( artworkUri )
    val onlineArtworkUri = if( isLocalArtwork )
        uploadArtwork( artworkUri )
    else
        artworkUri.toString().takeIf { it.startsWith("http") }

    return if ( onlineArtworkUri != null )
        getDiscordAssetUri(onlineArtworkUri, token)
    else
        getSmallImageUrl( token )
}

private suspend fun getSmallImageUrl( token: String ): String? =
    if ( ::smallImage.isInitialized )
        smallImage
    else
        getDiscordAssetUri("https://i.ibb.co/3mLGkPwY/app-logo.png", token)
            ?.also { smallImage = it }
//</editor-fold>

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
                largeImage = getLargeImageUrl( token, mediaItem.mediaMetadata.artworkUri ),
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