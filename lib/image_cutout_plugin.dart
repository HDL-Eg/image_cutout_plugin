import 'dart:typed_data';
import 'package:image_cutout_plugin/ImageResult.dart';
import 'image_cutout_plugin_platform_interface.dart';

class ImageCutoutPlugin {
  Future<ImageResult> cutoutImageData(Uint8List originData, Uint8List maskData,
      {bool antiAliasing = true, int antiRadius = 1}) {
    return ImageCutoutPluginPlatform.instance.cutoutImageData(
        originData, maskData,
        antiAliasing: antiAliasing, antiRadius: antiRadius);
  }

  Future<ImageResult> cutoutImageFile(String originPath, String maskPath,
      {bool antiAliasing = true, int antiRadius = 1}) {
    return ImageCutoutPluginPlatform.instance.cutoutImageFile(
        originPath, maskPath,
        antiAliasing: antiAliasing, antiRadius: antiRadius);
  }

  Future<ImageResult> cancel() {
    return ImageCutoutPluginPlatform.instance.cancel();
  }
}
