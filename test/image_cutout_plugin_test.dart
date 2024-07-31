import 'package:flutter_test/flutter_test.dart';
import 'package:image_cutout_plugin/image_cutout_plugin.dart';
import 'package:image_cutout_plugin/image_cutout_plugin_platform_interface.dart';
import 'package:image_cutout_plugin/image_cutout_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockImageCutoutPluginPlatform
    with MockPlatformInterfaceMixin
    implements ImageCutoutPluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final ImageCutoutPluginPlatform initialPlatform = ImageCutoutPluginPlatform.instance;

  test('$MethodChannelImageCutoutPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelImageCutoutPlugin>());
  });

  test('getPlatformVersion', () async {
    ImageCutoutPlugin imageCutoutPlugin = ImageCutoutPlugin();
    MockImageCutoutPluginPlatform fakePlatform = MockImageCutoutPluginPlatform();
    ImageCutoutPluginPlatform.instance = fakePlatform;

    expect(await imageCutoutPlugin.getPlatformVersion(), '42');
  });
}
