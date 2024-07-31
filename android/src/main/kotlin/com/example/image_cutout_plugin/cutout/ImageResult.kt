package com.example.image_cutout_plugin.cutout

sealed class ImageResult(
    val success: Boolean,
    open val code: Int = 0,
) {

    data class ImageSuccessResult(
        val result: ByteArray? = null,
        val path: String? = null,
        override val code: Int = 0
    ) :
        ImageResult(success = true, code = code)

    data class ImageErrorResult(val errorMsg: String = "", override val code: Int = 1000) :
        ImageResult(success = false, code = code)

    companion object {
        fun success(result: ByteArray) = ImageSuccessResult(result = result)
        fun success(path: String) = ImageSuccessResult(path = path)
        fun success() = ImageSuccessResult()
        fun error(type: ImageErrorType) =
            ImageErrorResult(errorMsg = type.message, code = type.code)
    }

    fun toMap(): Map<String, Any?> {
        return when (this) {
            is ImageSuccessResult -> mapOf(
                "success" to success,
                "code" to code,
                "result" to result,
                "path" to path,
            )

            is ImageErrorResult -> mapOf(
                "success" to success,
                "code" to code,
                "errorMsg" to errorMsg,
            )
        }
    }

}