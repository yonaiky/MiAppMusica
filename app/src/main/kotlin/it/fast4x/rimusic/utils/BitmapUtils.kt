package it.fast4x.rimusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import it.fast4x.rimusic.ui.styling.ColorPalette
import it.fast4x.rimusic.ui.styling.DefaultDarkColorPalette
import it.fast4x.rimusic.ui.styling.DefaultLightColorPalette
import it.fast4x.rimusic.ui.styling.dynamicColorPaletteOf
import it.fast4x.rimusic.ui.styling.hsl

suspend fun getBitmapFromUrl(context: Context, url: String): Bitmap {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
    val result = (loading.execute(request) as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}

fun getDynamicColorPaletteFromBitmap(bitmap: Bitmap): ColorPalette {
    val palette = Palette
            .from(bitmap)
            .maximumColorCount(8)
            .generate()

    return palette.dominantSwatch?.let { it.hsl.hsl.color.hsl.let { it1 ->
        dynamicColorPaletteOf(
            it1,false, false)
    } }
        ?: DefaultLightColorPalette

}


fun getDynamicColorPaletteFromBitmap(bitmap: Bitmap, isDark: Boolean, isPitchBlack: Boolean): ColorPalette {
    var palette: Palette? = null
    try {
        palette = Palette
            .from(bitmap)
            .maximumColorCount(8)
            .addFilter(if (isDark || isPitchBlack) ({ _, hsl -> hsl[0] !in 36f..100f }) else null)
            .generate()
    } catch (e: Exception) {
        //println("error palette ${e.message}")
    }

    val hsl = if (isDark || isPitchBlack) {
        palette?.dominantSwatch ?: Palette
            .from(bitmap)
            .maximumColorCount(8)
            .generate()
            .dominantSwatch
    } else {
        palette?.dominantSwatch
    }?.hsl ?: return if (isDark || isPitchBlack) DefaultLightColorPalette else DefaultLightColorPalette

    return if (hsl[1] < 0.08) {
        val newHsl = palette?.swatches
            ?.map(Palette.Swatch::getHsl)
            ?.sortedByDescending(FloatArray::component2)
            ?.find { it[1] != 0f }
            ?: hsl
        dynamicColorPaletteOf(newHsl, isDark, isPitchBlack)

    } else {
        dynamicColorPaletteOf(hsl, isDark, isPitchBlack)
    }
}