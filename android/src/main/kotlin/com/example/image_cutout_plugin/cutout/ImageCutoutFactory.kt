package com.example.image_cutout_plugin.cutout

object ImageCutoutFactory {

    fun createImageCutoutHandler(): ImageCutoutHandler {
        return ImageCutoutHandlerImpl()
    }
}