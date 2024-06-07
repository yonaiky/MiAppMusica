package it.fast4x.rimusic.service


import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.graphics.applyCanvas
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import it.fast4x.rimusic.utils.thumbnail
import timber.log.Timber

context(Context)
class BitmapProvider(
    private val bitmapSize: Int,
    private val colorProvider: (isSystemInDarkMode: Boolean) -> Int
) {
    var lastUri: Uri? = null
        private set

    var lastBitmap: Bitmap? = null
    private var lastIsSystemInDarkMode = false

    private var lastEnqueued: Disposable? = null

    private lateinit var defaultBitmap: Bitmap

    val bitmap: Bitmap
        get() = lastBitmap ?: defaultBitmap

    var listener: ((Bitmap?) -> Unit)? = null
        set(value) {
            field = value
            value?.invoke(lastBitmap)
        }

    init {
        setDefaultBitmap()
    }

    fun setDefaultBitmap(): Boolean {
        val isSystemInDarkMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (::defaultBitmap.isInitialized && isSystemInDarkMode == lastIsSystemInDarkMode) return false

        lastIsSystemInDarkMode = isSystemInDarkMode

        runCatching {
            defaultBitmap =
                Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888).applyCanvas {
                    drawColor(colorProvider(isSystemInDarkMode))
                }
        }.onFailure {
            Timber.e(it.message)
        }

        return lastBitmap == null
    }

    fun load(uri: Uri?, onDone: (Bitmap) -> Unit) {
        if (lastUri == uri) return

        lastEnqueued?.dispose()
        lastUri = uri

        runCatching {
            lastEnqueued = applicationContext.imageLoader.enqueue(
                ImageRequest.Builder(applicationContext)
                    .data(uri.thumbnail(bitmapSize))
                    .allowHardware(false)
                    .diskCacheKey(uri.thumbnail(bitmapSize).toString())
                    .memoryCacheKey(uri.thumbnail(bitmapSize).toString())
                    .listener(
                        onError = { _, result ->
                            Timber.e("Failed to load bitmap ${result.throwable.message}")
                            lastBitmap = null
                            onDone(bitmap)
                            listener?.invoke(lastBitmap)
                        },
                        onSuccess = { _, result ->
                            lastBitmap = (result.drawable as BitmapDrawable).bitmap
                            onDone(bitmap)
                            listener?.invoke(lastBitmap)
                        }
                    )
                    .build()
            )
        }.onFailure {
            Timber.e(it.message)
        }
    }
}
