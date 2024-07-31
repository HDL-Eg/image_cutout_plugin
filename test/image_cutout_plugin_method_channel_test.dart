import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:image_cutout_plugin/image_cutout_plugin_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelImageCutoutPlugin platform = MethodChannelImageCutoutPlugin();
  const MethodChannel channel = MethodChannel('image_cutout_plugin');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
