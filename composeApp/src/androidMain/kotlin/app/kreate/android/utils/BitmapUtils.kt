package app.kreate.android.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Contract
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Calculates the dimensions and position of a rectangular cut from a rectangular thumbnail
 * so that it can be scaled to fit a target rectangular screen.
 * The cut maximizes the area from the original picture while maintaining the screen's aspect ratio.
 *
 * @param thumbnailWidth The width of the original picture A.
 * @param thumbnailHeight The height of the original picture A.
 * will be inclusive. Default is false (exclusive).
 * @return A Rect object representing the cut-out portion (left, top, right, bottom)
 * relative to the original picture's top-left corner (0,0).
 * Returns null if input dimensions are invalid (e.g., non-positive).
 */
fun centerCropToMatchScreenSize(
    thumbnailWidth: Int,
    thumbnailHeight: Int
): Rect {
    val displayMetrics = Resources.getSystem().displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // Input validation
    require( thumbnailWidth > 0 && thumbnailHeight > 0 && screenWidth > 0 && screenHeight > 0 )

    val aspectRatioScreen = screenWidth.toFloat() / screenHeight.toFloat()
    val aspectRatioPicture = thumbnailWidth.toFloat() / thumbnailHeight.toFloat()

    val cutWidth: Float
    val cutHeight: Float
    if ( aspectRatioPicture > aspectRatioScreen ) {
        // Picture is relatively wider than the screen.
        // We will use the full picture height and calculate the necessary cut width.
        cutHeight = thumbnailHeight.toFloat()
        cutWidth = cutHeight * aspectRatioScreen
    } else {
        // Picture is relatively taller or equally proportioned as the screen.
        // We will use the full picture width and calculate the necessary cut height.
        cutWidth = thumbnailWidth.toFloat()
        cutHeight = cutWidth / aspectRatioScreen
    }

    // Calculate top-left coordinates for centering the cut
    val cutLeft = ((thumbnailWidth - cutWidth) / 2).toInt()
    val cutTop = ((thumbnailHeight - cutHeight) / 2).toInt()
    val cutRight = cutLeft + cutWidth.toInt()
    val cutBottom = cutTop + cutHeight.toInt()

    return Rect(cutLeft, cutTop, cutRight, cutBottom)
}

/**
 * Crops a Bitmap based on the provided Rect.
 *
 * @param bitmap The original Bitmap to crop.
 * @param rect The [Rect] defining the area to crop.
 * @return A new Bitmap representing the cropped portion, or null if cropping fails.
 */
@Contract("_,_->new")
fun centerCropBitmap(bitmap: Bitmap, rect: Rect): Bitmap? {
    val x = rect.left
    val y = rect.top
    val width = rect.width()
    val height = rect.height()

    // Ensure the rect dimensions are valid and within the original bitmap bounds
    require(
        x >= 0
            && y >= 0
            && x + width <= bitmap.width
            && y + height <= bitmap.height
            && width > 0
            && height > 0
    ) { "Crop rectangle is out of bounds or has invalid dimensions." }

    return Bitmap.createBitmap(bitmap, x, y, width, height)
}

/**
 * An improvised implementation of [T8RIN/BlurTransformation](https://github.com/T8RIN/BlurTransformation)
 */
suspend fun Bitmap.blur( scale: Float, radius: Int ): Bitmap =
    withContext(Dispatchers.IO) {
        val originalConfig = this@blur.config
        if( originalConfig == null || radius < 1 ) return@withContext this@blur

        val w = (this@blur.width * scale).roundToInt()
        val h = (this@blur.height * scale).roundToInt()
        val bitmap: Bitmap = this@blur.scale( w, h, false )
                                      .copy( originalConfig, true )

        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        val vmin = IntArray(w.coerceAtLeast(h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        var yw: Int = yi
        val stack = Array(div) {
            IntArray(
                3
            )
        }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + wm.coerceAtMost(i.coerceAtLeast(0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = (x + radius + 1).coerceAtMost(wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = 0.coerceAtLeast(yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = (y + r1).coerceAtMost(hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }

        //merge #2258
        val darkenFactor = 0.2f // 20% darker

        for (i in 0 until wh) {
            val alpha = Color.alpha(pix[i])
            val red = (Color.red(pix[i]) * (1 - darkenFactor)).toInt()
            val green = (Color.green(pix[i]) * (1 - darkenFactor)).toInt()
            val blue = (Color.blue(pix[i]) * (1 - darkenFactor)).toInt()

            pix[i] = Color.argb(alpha, red, green, blue)
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        this@blur.recycle()
        return@withContext bitmap
    }
