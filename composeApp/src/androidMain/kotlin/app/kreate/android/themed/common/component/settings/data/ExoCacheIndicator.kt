package app.kreate.android.themed.common.component.settings.data

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import app.kreate.android.Preferences

@UnstableApi
class ExoCacheIndicator(
    private val preference: Preferences.Long,
    private val cache: Cache
): CacheUsageIndicator() {

    override fun updateProgress() {
        super.progress = cache.cacheSpace.toFloat() / preference.value
    }

    override fun onConfirm() {
        cache.keys.forEach( cache::removeResource )
        updateProgress()
        hideDialog()
    }
}