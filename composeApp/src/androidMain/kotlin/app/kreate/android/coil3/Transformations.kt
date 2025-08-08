package app.kreate.android.coil3

import android.graphics.Bitmap
import app.kreate.android.utils.blur
import coil3.size.Size
import coil3.transform.Transformation

class BlurTransformation(
    val radius: Int,
    val scale: Float
) : Transformation() {

    override val cacheKey: String = "${javaClass.name}-$radius"

    override suspend fun transform( input: Bitmap, size: Size ) = input.blur( scale, radius )

    override fun equals( other: Any? ): Boolean =
        this === other || (other is BlurTransformation && radius == other.radius)

    override fun hashCode(): Int = radius.hashCode()
}