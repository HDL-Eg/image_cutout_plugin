package com.example.image_cutout_plugin.cutout

import androidx.annotation.WorkerThread

interface ImageCutoutHandler {

    @WorkerThread
    fun cutoutData(
        origin: ByteArray?,
        mask: ByteArray?,
        antiAliasing: Boolean = true,
        antiRadius: Int = 1
    ): ImageResult

    @WorkerThread
    fun cutoutPath(
        origin: String?,
        mask: String?,
        antiAliasing: Boolean = true,
        antiRadius: Int = 1
    ): ImageResult
}