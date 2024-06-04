package it.fast4x.rimusic

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.kieronquinn.monetcompat.core.MonetCompat
import it.fast4x.rimusic.enums.CoilDiskCacheMaxSize
import it.fast4x.rimusic.utils.FileLoggingTree
import it.fast4x.rimusic.utils.coilDiskCacheMaxSizeKey
import it.fast4x.rimusic.utils.getEnum
import it.fast4x.rimusic.utils.logDebugEnabledKey
import it.fast4x.rimusic.utils.preferences
import timber.log.Timber
import java.io.File

class MainApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        DatabaseInitializer()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .placeholder(R.drawable.loader)
            .error(R.drawable.app_icon)
            .fallback(R.drawable.app_icon)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache(
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            )
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache(
                DiskCache.Builder()
                    .directory(filesDir.resolve("coil"))
                    .maxSizeBytes(
                        preferences.getEnum(
                            coilDiskCacheMaxSizeKey,
                            CoilDiskCacheMaxSize.`128MB`
                        ).bytes
                    )
                    .build()
            )
            .respectCacheHeaders(false)
            .build()
    }
}
