import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:image_cutout_plugin/ImageResult.dart';
import 'image_cutout_plugin_platform_interface.dart';

class MethodChannelImageCutoutPlugin extends ImageCutoutPluginPlatform {
  @visibleForTesting
  final methodChannel = const MethodChannel('image_cutout_plugin');

  @override
  Future<ImageResult> cutoutImageData(Uint8List originData, Uint8List maskData,
      {bool antiAliasing = true, int antiRadius = 1}) async {
    final resultMap =
        await methodChannel.invokeMethod<Map<dynamic, dynamic>>("cutout", {
      "originData": originData,
      "maskData": maskData,
      "antiAliasing": antiAliasing,
      "antiRadius": antiRadius,
    });
    return ImageResult.fromMap(resultMap!);
  }

  @override
  Future<ImageResult> cutoutImageFile(String originPath, String maskPath,
      {bool antiAliasing = true, int antiRadius = 1}) async {
    final resultMap =
        await methodChannel.invokeMethod<Map<dynamic, dynamic>>("cutout", {
      "originPath": originPath,
      "maskPath": maskPath,
      "antiAliasing": antiAliasing,
      "antiRadius": antiRadius,
    });
    return ImageResult.fromMap(resultMap!);
  }

  @override
  Future<ImageResult> cancel() async {
    final resultMap =
        await methodChannel.invokeMethod<Map<dynamic, dynamic>>("cancel");
    final imageResult = ImageResult.fromMap(resultMap!);
    return imageResult;
  }
}
