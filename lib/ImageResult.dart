import 'dart:typed_data';

class ImageResult {
  final bool success;
  final int code;
  final Uint8List? result;
  final String? path;
  final String? errorMsg;

  ImageResult({
    required this.success,
    required this.code,
    this.result,
    this.path,
    this.errorMsg,
  });

  factory ImageResult.fromMap(Map<dynamic, dynamic> map) {
    return ImageResult(
      success: map['success'] as bool,
      code: map['code'] as int,
      result: map['result'] as Uint8List?,
      path: map['path'] as String?,
      errorMsg: map['errorMsg'] as String?,
    );
  }
}
