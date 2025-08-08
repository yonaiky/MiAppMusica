package app.kreate.android.themed.common.component.settings.data

import coil3.disk.DiskCache

class ImageCacheIndicator(private val diskCache: DiskCache): CacheUsageIndicator() {

    override fun updateProgress() {
        super.progress = diskCache.size.toFloat() / diskCache.maxSize
    }

    override fun onConfirm() {
        diskCache.clear()
        updateProgress()
        hideDialog()
    }
}