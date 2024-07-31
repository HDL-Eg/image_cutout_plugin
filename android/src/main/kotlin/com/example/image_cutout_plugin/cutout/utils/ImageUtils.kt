package com.example.image_cutout_plugin.cutout.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.annotation.ColorInt
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

object ImageUtils {

    fun processImageWithMask(
        origin: Bitmap,
        mask: Bitmap,
        @ColorInt color: Int = Color.BLACK,
        antiAliasing: Boolean = true,
        antiRadius: Int = 1,
    ) {
        ImageTaskManager.ensureActive()
        for (pixelY in 0 until mask.height) {
            for (pixelX in 0 until mask.width) {
                ImageTaskManager.ensureActive()
                val pixel = mask.getPixel(pixelX, pixelY)
                if (antiAliasing) {
                    val alpha = calculateAlpha(mask, pixelX, pixelY, antiRadius)
                    if (alpha < 255) {
                        val originalPixel = origin.getPixel(pixelX, pixelY)
                        val newPixel = Color.argb(
                            alpha,
                            Color.red(originalPixel),
                            Color.green(originalPixel),
                            Color.blue(originalPixel)
                        )
                        origin.setPixel(pixelX, pixelY, newPixel)
                    }
                }
                if (pixel == color) {
                    origin.setPixel(pixelX, pixelY, Color.TRANSPARENT)
                }
            }
        }
    }

    private fun calculateAlpha(maskBitmap: Bitmap, x: Int, y: Int, radius: Int = 1): Int {
        ImageTaskManager.ensureActive()
        val antiRadius = if (radius < 1) { 1 } else { radius }
        var totalAlpha = 0
        var count = 0
        for (dy in -antiRadius..antiRadius) {
            for (dx in -antiRadius..antiRadius) {
                ImageTaskManager.ensureActive()
                val nx = x + dx
                val ny = y + dy
                if (nx >= 0 && nx < maskBitmap.width && ny >= 0 && ny < maskBitmap.height) {
                    val pixel = maskBitmap.getPixel(nx, ny)
                    val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                    totalAlpha += if (gray < 128) 0 else 255
                    count++
                }
            }
        }
        return totalAlpha / count
    }

    fun processImageCutout(
        maskHandled: Bitmap,
        canvas: Canvas,
        paint: Paint,
    ) {
        ImageTaskManager.ensureActive()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(maskHandled, 0f, 0f, paint)
        paint.xfermode = null
    }

    fun createImageDataResult(bitmapResult: Bitmap): ByteArray {
        ImageTaskManager.ensureActive()
        val outputStream = ByteArrayOutputStream()
        bitmapResult.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    fun createImageLocalStorageResult(bitmapResult: Bitmap, outputFileDir : String = "/sdcard/cutout"): String {
        ImageTaskManager.ensureActive()
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        val fileName = "cutout-${formatter.format(date)}.png"
        val dir = File(outputFileDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val outputFile = File(dir, fileName)
        val outputStream = FileOutputStream(outputFile)
        bitmapResult.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputFile.absolutePath
    }

    fun clearBitmapResource(vararg bitmaps: Bitmap) {
        for (bitmap in bitmaps) {
            try {
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}