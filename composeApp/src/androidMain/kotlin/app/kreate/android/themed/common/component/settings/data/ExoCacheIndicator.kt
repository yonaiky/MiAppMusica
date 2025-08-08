package app.kreate.android.themed.common.component.settings.data

import androidx.compose.runtime.getValue
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import app.kreate.android.Preferences
import it.fast4x.rimusic.enums.ExoPlayerDiskCacheMaxSize

@UnstableApi
class ExoCacheIndicator(
    private val cache: Cache
): CacheUsageIndicator() {

    override fun updateProgress() {
        val exoPlayerDiskCacheMaxSize by Preferences.SONG_CACHE_SIZE
        val maxSize = when( exoPlayerDiskCacheMaxSize ) {
            ExoPlayerDiskCacheMaxSize.Custom    -> Preferences.SONG_CACHE_CUSTOM_SIZE.value * 1000L * 1000
            else                                -> exoPlayerDiskCacheMaxSize.bytes
        }

        super.progress = cache.cacheSpace.toFloat() / maxSize
    }

    override fun onConfirm() {
        cache.keys.forEach( cache::removeResource )
        updateProgress()
        hideDialog()
    }
}