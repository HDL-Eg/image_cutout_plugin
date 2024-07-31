import 'dart:typed_data';
import 'package:image_cutout_plugin/ImageResult.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'image_cutout_plugin_method_channel.dart';

abstract class ImageCutoutPluginPlatform extends PlatformInterface {
  ImageCutoutPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static ImageCutoutPluginPlatform _instance = MethodChannelImageCutoutPlugin();

  static ImageCutoutPluginPlatform get instance => _instance;

  static set instance(ImageCutoutPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<ImageResult> cutoutImageData(
    Uint8List originData,
    Uint8List maskData, {
    bool antiAliasing = true,
    int antiRadius = 1,
  });

  Future<ImageResult> cutoutImageFile(
    String originPath,
    String maskPath, {
    bool antiAliasing = true,
    int antiRadius = 1,
  });

  Future<ImageResult> cancel();
}
