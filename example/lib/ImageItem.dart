import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';

class ImageItem extends StatelessWidget {
  final String? imageAssetPath;
  final String? imageFilePath;
  final Uint8List? imageArray;
  final int width;
  final int height;

  const ImageItem({
    super.key,
    this.imageAssetPath,
    this.imageFilePath,
    this.imageArray,
    this.width = 220,
    this.height = 220,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      alignment: Alignment.center,
      child: buildImageItem(),
    );
  }

  Widget buildImageItem() {
    if (imageAssetPath != null) {
      return Image.asset(
        imageAssetPath!,
        fit: BoxFit.cover,
        width: width.toDouble(),
        height: height.toDouble(),
      );
    }
    if (imageFilePath != null) {
      return Image.file(
        File(imageFilePath!),
        fit: BoxFit.cover,
        width: width.toDouble(),
        height: height.toDouble(),
      );
    }
    if (imageArray != null) {
      return Image.memory(
        imageArray!,
        fit: BoxFit.cover,
        width: width.toDouble(),
        height: height.toDouble(),
      );
    }
    return Container(
      width: width.toDouble(),
      height: height.toDouble(),
      alignment: Alignment.center,
      child: FlutterLogo(
        size: width.toDouble() * 0.8,
      ),
    );
  }
}
