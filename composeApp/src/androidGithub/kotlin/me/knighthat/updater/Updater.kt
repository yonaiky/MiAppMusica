package me.knighthat.updater

import android.os.Looper
import androidx.compose.ui.util.fastFirstOrNull
import app.kreate.android.BuildConfig
import app.kreate.android.Preferences
import app.kreate.android.R
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.enums.CheckUpdateState
import it.fast4x.rimusic.utils.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.knighthat.updater.Updater.build
import me.knighthat.utils.Repository
import me.knighthat.utils.Toaster
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.file.NoSuchFileException

object Updater {
    private lateinit var tagName: String

    lateinit var build: GithubRelease.Build

    private fun extractBuild( assets: List<GithubRelease.Build> ): GithubRelease.Build {
        return assets.fastFirstOrNull {
            // Get the first build that has name matches 'Kreate-<buildType>.apk'
            // e.g. Release version will have name 'Kreate-release.apk'
            it.name == "%s-%s.apk".format( BuildConfig.APP_NAME, BuildConfig.BUILD_TYPE )
        } ?: throw NoSuchFileException("Couldn't find build matching this build")       // TODO: Add to strings.xml
    }

    /**
     * Turns `v1.0.0` to `1.0.0`, `1.0.0-m` to `1.0.0`
     */
    private fun trimVersion( versionStr: String ) = versionStr.removePrefix( "v" ).substringBefore( "-" )

    /**
     * Sends out requests to Github for latest version.
     *
     * Results are downloaded, filtered, and saved to [build]
     *
     * > **NOTE**: This is a blocking process, it should never run on UI thread
     */
    private suspend fun fetchUpdate() = withContext( Dispatchers.IO ) {
        assert( Looper.myLooper() != Looper.getMainLooper() ) {
            "Cannot run fetch update on main thread"
        }

        // https://api.github.com/repos/knighthat/Kreate/releases/latest
        val url = "${Repository.GITHUB_API}/repos/${Repository.LATEST_TAG_URL}"
        val request = Request.Builder().url( url ).build()
        val response = OkHttpClient().newCall( request ).execute()

        if( !response.isSuccessful ) {
            Toaster.e( response.message )
            return@withContext
        }

        val resBody = response.body?.string()
        if( resBody.isNullOrBlank() ) {
            Toaster.i( R.string.info_no_update_available )
            return@withContext
        }

        val json = Json {
            ignoreUnknownKeys = true
        }
        val githubRelease = json.decodeFromString<GithubRelease>( resBody )
        build = extractBuild( githubRelease.builds )
        tagName = githubRelease.tagName
    }

    fun checkForUpdate(
        isForced: Boolean = false
    ) = CoroutineScope( Dispatchers.IO ).launch {
        if( ::build.isInitialized && !isForced )
            return@launch

        if( !isNetworkAvailable( appContext() ) ) {
            Toaster.noInternet()
            return@launch
        }

        try {
            fetchUpdate()

            val isNewUpdateAvailable = trimVersion( BuildConfig.VERSION_NAME ) != trimVersion( tagName )
            if( !isNewUpdateAvailable )
                Toaster.i( R.string.info_no_update_available )

            when( Preferences.CHECK_UPDATE.value ) {
                CheckUpdateState.ASK                -> NewUpdatePrompt.isActive = isNewUpdateAvailable
                CheckUpdateState.DOWNLOAD_INSTALL   -> DownloadAndInstallDialog.isActive = isNewUpdateAvailable
                CheckUpdateState.DISABLED           -> NewUpdatePrompt.isActive = isForced && isNewUpdateAvailable
            }
        } catch( e: Exception ) {
            Toaster.e( e.message ?: appContext().getString( R.string.error_unknown ) )
        }
    }
}