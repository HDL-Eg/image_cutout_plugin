package com.example.image_cutout_plugin

import com.example.image_cutout_plugin.cutout.ImageCutoutFactory
import com.example.image_cutout_plugin.cutout.ImageErrorType
import com.example.image_cutout_plugin.cutout.ImageResult
import com.example.image_cutout_plugin.cutout.utils.ImageTaskManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class ImageCutoutPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel

    companion object {
        const val PLUGIN = "image_cutout_plugin"

        const val METHOD_CUTOUT = "cutout"
        const val PARAM_ORIGIN_DATA = "originData"
        const val PARAM_MASK_DATA = "maskData"
        const val PARAM_ORIGIN_PATH = "originPath"
        const val PARAM_MASK_PATH = "maskPath"
        const val PARAM_ANTI_ALIASING = "antiAliasing"
        const val PARAM_ANTI_RADIUS = "antiRadius"

        const val METHOD_CANCEL = "cancel"
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, PLUGIN)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            METHOD_CUTOUT -> {
                handleImageCutOut(call, result)
            }

            METHOD_CANCEL -> {
                handleCancelCutOut(result)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    private fun handleImageCutOut(call: MethodCall, result: Result) {
        val originData = call.argument<ByteArray>(PARAM_ORIGIN_DATA)
        val maskData = call.argument<ByteArray>(PARAM_MASK_DATA)
        val originPath = call.argument<String>(PARAM_ORIGIN_PATH)
        val maskPath = call.argument<String>(PARAM_MASK_PATH)
        val antiAliasing = call.argument<Boolean>(PARAM_ANTI_ALIASING)
        val antiRadius = call.argument<Int>(PARAM_ANTI_RADIUS)

        ImageTaskManager.cancel()
        val job = ImageTaskManager.getTaskScope().launch {
            val cutoutHandler = ImageCutoutFactory.createImageCutoutHandler()
            try {
                val imageResult = if (originPath != null && maskPath != null) {
                    cutoutHandler.cutoutPath(
                        originPath,
                        maskPath,
                        antiAliasing ?: true,
                        antiRadius ?: 1,
                    )
                } else {
                    cutoutHandler.cutoutData(
                        originData,
                        maskData,
                        antiAliasing ?: true,
                        antiRadius ?: 1,
                    )
                }
                if (isActive) {
                    val resultMap = imageResult.toMap()
                    withContext(Dispatchers.Main) {
                        result.success(resultMap.toMap())
                    }
                }
            } catch (e: CancellationException) {
                withContext(Dispatchers.Main) {
                    val resultMap = ImageResult.error(ImageErrorType.ERROR_CANCEL).toMap()
                    result.success(resultMap)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val resultMap =
                        ImageResult.error(ImageErrorType.ERROR_HANDLE_CUTOUT_RESULT_ERROR).toMap()
                    result.success(resultMap)
                }
            }
        }
        ImageTaskManager.updateCurrentJob(job)
        job.invokeOnCompletion { throwable ->
            if (throwable is CancellationException) {
                val resultMap = ImageResult.error(ImageErrorType.ERROR_CANCEL).toMap()
                result.success(resultMap)
            }
        }
    }

    private fun handleCancelCutOut(result: Result) {
        ImageTaskManager.cancel()
        result.success(ImageResult.success().toMap())
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        ImageTaskManager.cancel()
    }
}
