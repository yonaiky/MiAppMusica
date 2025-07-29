package app.kreate.android.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import org.jetbrains.annotations.Contract

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
