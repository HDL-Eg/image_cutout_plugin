package com.example.image_cutout_plugin.cutout

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.WorkerThread
import com.example.image_cutout_plugin.cutout.utils.ImageUtils
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

internal class ImageCutoutHandlerImpl : ImageCutoutHandler {

    @WorkerThread
    override fun cutoutData(
        origin: ByteArray?,
        mask: ByteArray?,
        antiAliasing: Boolean,
        antiRadius: Int,
    ): ImageResult {
        try {
            val validateNotNull = validateImageNotNull(origin, mask)
            if (!validateNotNull) {
                return ImageResult.error(ImageErrorType.ERROR_IMAGE_NULL)
            }
            val validateBitmapSize = validateBitmapSize(origin!!, mask!!)
            if (!validateBitmapSize) {
                return ImageResult.error(ImageErrorType.ERROR_SIZE_NOT_MATCH)
            }
            return handleImageDataCutout(origin, mask, antiAliasing, antiRadius)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ImageResult.error(ImageErrorType.ERROR_HANDLE_CUTOUT_IMAGE_ERROR)
        }
    }

    override fun cutoutPath(
        origin: String?,
        mask: String?,
        antiAliasing: Boolean,
        antiRadius: Int
    ): ImageResult {
        try {
            val validateNotNull = validatePathNotNull(origin, mask)
            if (!validateNotNull) {
                return ImageResult.error(ImageErrorType.ERROR_IMAGE_NULL)
            }
            val validateFileExist = validateFileExist(origin!!, mask!!)
            if (!validateFileExist) {
                return ImageResult.error(ImageErrorType.ERROR_IMAGE_NOT_FOUND)
            }
            val validateBitmapSize = validateBitmapSize(origin, mask)
            if (!validateBitmapSize) {
                return ImageResult.error(ImageErrorType.ERROR_SIZE_NOT_MATCH)
            }
            return handleImagePathCutout(origin, mask, antiAliasing, antiRadius)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ImageResult.error(ImageErrorType.ERROR_HANDLE_CUTOUT_IMAGE_ERROR)
        }
    }

    @WorkerThread
    private fun validateImageNotNull(origin: ByteArray?, mask: ByteArray?): Boolean {
        if (origin == null || mask == null) {
            return false
        }
        if (origin.isEmpty() || mask.isEmpty()) {
            return false
        }
        return true
    }

    @WorkerThread
    private fun validatePathNotNull(origin: String?, mask: String?): Boolean {
        if (origin == null || mask == null) {
            return false
        }
        if (origin.isEmpty() || mask.isEmpty()) {
            return false
        }
        return true
    }

    @WorkerThread
    private fun validateBitmapSize(origin: ByteArray, mask: ByteArray): Boolean {
        try {
            val optionsOrigin = BitmapFactory.Options()
            optionsOrigin.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(origin, 0, origin.size, optionsOrigin)

            val optionsMask = BitmapFactory.Options()
            optionsMask.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(mask, 0, mask.size, optionsMask)

            if (optionsOrigin.outWidth != optionsMask.outWidth) {
                return false
            }
            if (optionsOrigin.outHeight != optionsMask.outHeight) {
                return false
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    @WorkerThread
    private fun validateFileExist(origin: String, mask: String): Boolean {
        try {
            val originFile = File(origin)
            val maskFile = File(mask)
            return originFile.exists() && maskFile.exists()
        } catch (e: Exception) {
            return false
        }
    }

    @WorkerThread
    private fun validateBitmapSize(origin: String, mask: String): Boolean {
        try {
            val optionsOrigin = BitmapFactory.Options()
            optionsOrigin.inJustDecodeBounds = true
            BitmapFactory.decodeFile(origin, optionsOrigin)

            val optionsMask = BitmapFactory.Options()
            optionsMask.inJustDecodeBounds = true
            BitmapFactory.decodeFile(mask, optionsMask)

            if (optionsOrigin.outWidth != optionsMask.outWidth) {
                return false
            }
            if (optionsOrigin.outHeight != optionsMask.outHeight) {
                return false
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    @WorkerThread
    private fun handleImageDataCutout(
        origin: ByteArray,
        mask: ByteArray,
        antiAliasing: Boolean = true,
        antiRadius: Int = 1,
    ): ImageResult {
        try {
            val options = BitmapFactory.Options().apply {
                inMutable = true
            }
            val bitmapOrigin = BitmapFactory.decodeByteArray(origin, 0, origin.size, options)
            val bitmapMask = BitmapFactory.decodeByteArray(mask, 0, mask.size, options)
            return handleImageBitmapCutout(bitmapOrigin, bitmapMask, antiAliasing, antiRadius, ImageStorageType.MEMORY)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ImageResult.error(ImageErrorType.ERROR_HANDLE_CUTOUT_IMAGE_ERROR)
        }
    }

    @WorkerThread
    private fun handleImagePathCutout(
        origin: String,
        mask: String,
        antiAliasing: Boolean = true,
        antiRadius: Int = 1,
    ): ImageResult {
        try {
            val options = BitmapFactory.Options().apply {
                inMutable = true
            }
            val bitmapOrigin = BitmapFactory.decodeFile(origin, options)
            val bitmapMask = BitmapFactory.decodeFile(mask, options)
            return handleImageBitmapCutout(bitmapOrigin,bitmapMask, antiAliasing, antiRadius, ImageStorageType.LOCAL_STORAGE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ImageResult.error(ImageErrorType.ERROR_HANDLE_CUTOUT_IMAGE_ERROR)
        }
    }

    @WorkerThread
    private fun handleImageBitmapCutout(
        bitmapOrigin: Bitmap,
        bitmapMask: Bitmap,
        antiAliasing: Boolean = true,
        antiRadius: Int = 1,
        imageStorageType: ImageStorageType = ImageStorageType.MEMORY,
    ): ImageResult {

        val bitmapResult = Bitmap.createBitmap(
            bitmapOrigin.width,
            bitmapOrigin.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmapResult)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(bitmapOrigin, 0f, 0f, paint)

        ImageUtils.processImageWithMask(
            bitmapOrigin,
            bitmapMask,
            antiAliasing = antiAliasing,
            antiRadius = antiRadius
        )

        ImageUtils.processImageCutout(bitmapOrigin, canvas, paint)

        val result : ImageResult = when(imageStorageType) {
            ImageStorageType.MEMORY -> {
                val resultImage = ImageUtils.createImageDataResult(bitmapResult)
                ImageResult.success(result = resultImage)
            }
            ImageStorageType.LOCAL_STORAGE -> {
                val resultPath = ImageUtils.createImageLocalStorageResult(bitmapResult)
                ImageResult.success(path = resultPath)
            }
        }

        ImageUtils.clearBitmapResource(bitmapOrigin, bitmapMask, bitmapResult)

        return result
    }
}