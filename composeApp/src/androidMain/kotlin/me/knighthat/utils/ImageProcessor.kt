package me.knighthat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.FileOutputStream
import java.io.IOException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.io.path.createTempFile
import kotlin.math.roundToInt

object ImageProcessor {

    private fun calculateInSampleSize(
        srcWidth: Int,
        srcHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        if (srcHeight > reqHeight || srcWidth > reqWidth) {
            val halfHeight: Int = srcHeight / 2
            val halfWidth: Int = srcWidth / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than or equal to the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
                inSampleSize *= 2
        }
        return inSampleSize
    }

    private fun queryImageInfo( context: Context, artworkUri: Uri ): Triple<Int, Int, Long> {
        var srcWidth = 0
        var srcHeight = 0
        var srcSize = 0L

        val projection = arrayOf(
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.SIZE
        )
        context.contentResolver
               .query( artworkUri, projection, null, null, null )!!
               .use { cursor ->
                   if( cursor.count <= 0 ) return@use

                   val widthCol = cursor.getColumnIndex( MediaStore.Images.Media.WIDTH )
                   val heightCol = cursor.getColumnIndex( MediaStore.Images.Media.HEIGHT )
                   val sizeCol = cursor.getColumnIndex( MediaStore.Images.Media.SIZE )
                   while( cursor.moveToNext() ) {
                       srcWidth = cursor.getInt( widthCol )
                       srcHeight = cursor.getInt( heightCol )
                       srcSize = cursor.getLong( sizeCol )
                   }
               }

        return Triple(srcWidth, srcHeight, srcSize)
    }

    /**
     * Processes an image from a given Uri:
     *
     * 1. If it's a remote Uri (e.g., http/https), returns it directly.
     * 2. If it's a local Uri (file/content):
     * a. Verifies if it's an image by its MIME type.
     * b. Checks if dimensions exceed MAX_DIMENSION or file size exceeds MAX_FILE_SIZE_BYTES.
     * c. If limits are exceeded, scales and compresses the image to meet specs.
     * d. If not an image, throws IllegalArgumentException.
     *
     * @param context The application context
     * @param artworkUri The Uri of the image file (can be local or remote)
     * @return A Uri to the processed image (original local, compressed/scaled local, or original remote Uri)
     * @throws IllegalArgumentException if the local Uri does not point to an image or local processing fails
     * @throws SecurityException due to lack of permission
     * @throws IOException other read/write related issues
     * @throws OutOfMemoryError when heap is overflown
     */
    @OptIn(ExperimentalContracts::class)
    @Throws(
        IllegalArgumentException::class,
        SecurityException::class,
        IOException::class,
        OutOfMemoryError::class
    )
    fun compressArtwork( context: Context, artworkUri: Uri?, maxWidth: Int, maxHeight: Int, maxSize: Long ): Uri? {
        contract {
            returns( null ) implies ( artworkUri == null )
        }
        if( artworkUri == null ) return null

        val scheme = artworkUri.scheme
        if (scheme == "http" || scheme == "https") {
            return artworkUri
        }

        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType( artworkUri )
        if ( mimeType == null || !mimeType.startsWith( "image/" ) )
            throw IllegalArgumentException("The provided URI does not point to an image file. MIME type: $mimeType")

        val (originalWidth, originalHeight, originalFileSize) = queryImageInfo( context, artworkUri )
        val needsResizing = originalWidth > maxWidth || originalHeight > maxHeight
        val needsCompression = originalFileSize > maxSize
        if ( !needsResizing && !needsCompression ) return artworkUri

        // Step 5: Process and compress if local limits exceeded
        try {
            // Determine the target dimensions
            val scale = if( needsResizing )
                minOf( maxWidth.toFloat() / originalWidth, maxHeight.toFloat() / originalHeight )
            else
                1f
            val targetWidth = (originalWidth * scale).roundToInt()
            val targetHeight = (originalHeight * scale).roundToInt()

            val decodedBitmap = contentResolver.openInputStream( artworkUri )!!.use { inputStream ->
                // Calculate inSampleSize based on target dimensions for decoding
                val decodeOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inSampleSize = calculateInSampleSize(originalWidth, originalHeight, targetWidth, targetHeight)
                }

                BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            }
            if (decodedBitmap == null)
                throw IllegalArgumentException("Failed to decode image into Bitmap from URI: $artworkUri")

            val outputFile = createTempFile( "ImageProcessor_compressArtwork_${System.currentTimeMillis()}" ).toFile()
            outputFile.deleteOnExit()

            FileOutputStream(outputFile).use { outStream ->
                val success = decodedBitmap.compress(Bitmap.CompressFormat.PNG, 70, outStream)
                decodedBitmap.recycle() // Release memory

                if ( !success )
                    throw IOException("Failed to compress image to file: ${outputFile.absolutePath}")
            }

            return Uri.fromFile( outputFile )

        } catch (e: OutOfMemoryError) {
            System.gc()
            throw e
        }
    }
}