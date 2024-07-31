package com.example.image_cutout_plugin.cutout

enum class ImageErrorType(
    val message: String,
    val code: Int,
) {
    ERROR_IMAGE_NOT_FOUND(
        message = "origin or mask image is not found",
        code = 1000
    ),
    ERROR_IMAGE_NULL(
        message = "origin or mask image is null",
        code = 1001
    ),
    ERROR_SIZE_NOT_MATCH(
        message = "origin image's size is not same as mask image's size",
        code = 1002,
    ),
    ERROR_HANDLE_CUTOUT_IMAGE_ERROR(
        message = "handle cutout error",
        code = 1003,
    ),
    ERROR_HANDLE_CUTOUT_RESULT_ERROR(
        message = "handle cutout result error",
        code = 1004,
    ),
    ERROR_CANCEL(
        message = "cancel",
        code = 2000,
    ),
}