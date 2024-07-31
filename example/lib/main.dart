import 'dart:async';

import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:image_cutout_plugin/ImageResult.dart';
import 'package:image_cutout_plugin/image_cutout_plugin.dart';
import 'package:image_cutout_plugin_example/ImageItem.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final String originAssetPath = "assets/images/origin.png";
  final String maskAssetPath = "assets/images/mask.jpeg";
  final String originFilePath = "/sdcard/origin.png";
  final String maskFilePath = "/sdcard/mask.jpeg";

  final _imageCutoutPlugin = ImageCutoutPlugin();
  bool _antiAliasing = false;

  Future<ImageResult>? _futureImageResult;
  ImageResult? _imageResult;

  bool _permissionGranted = false;

  @override
  void initState() {
    super.initState();
    _requestStoragePermission();
  }

  Future<void> _requestStoragePermission() async {
    PermissionStatus status = await Permission.manageExternalStorage.request();
    setState(() {
      _permissionGranted = status.isGranted;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: SafeArea(
            child: _permissionGranted
                ? _buildCutoutPage()
                : _buildPermissionPage()),
      ),
    );
  }

  Widget _buildPermissionPage() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Text("No Permission, please grant storage permission."),
          ElevatedButton(
              onPressed: () {
                _requestStoragePermission();
              },
              child: const Text("Request Permission"))
        ],
      ),
    );
  }

  Widget _buildCutoutPage() {
    return SingleChildScrollView(
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Column(
                children: [
                  const SizedBox(height: 10),
                  ElevatedButton(
                    onPressed: _futureImageResult == null
                        ? () {
                            setState(() {
                              _futureImageResult = _cutoutAssetImage();
                            });
                          }
                        : null,
                    child: const Text("Cut-1"),
                  ),
                  ElevatedButton(
                    onPressed: _futureImageResult == null
                        ? () {
                            setState(() {
                              _futureImageResult = _cutoutPathImage();
                            });
                          }
                        : null,
                    child: const Text("Cut-2"),
                  ),
                ],
              ),
              const SizedBox(width: 10),
              ElevatedButton(
                onPressed: _futureImageResult != null
                    ? () {
                        _cancel();
                      }
                    : null,
                child: const Text("Cancel"),
              ),
              const SizedBox(width: 10),
              Row(
                children: [
                  const Text('antiAliasing'),
                  Checkbox(
                    value: _antiAliasing,
                    onChanged: (bool? value) {
                      setState(() {
                        _antiAliasing = value ?? false;
                      });
                    },
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(height: 10),
          ImageItem(imageFilePath: originFilePath),
          ImageItem(imageFilePath: maskFilePath),
          _buildCutoutResultImage(),
        ],
      ),
    );
  }

  Future<ImageResult> _cutoutAssetImage() async {
    final originImageData = await rootBundle.load(originAssetPath);
    final maskImageData = await rootBundle.load(maskAssetPath);
    final originArrayData = originImageData.buffer.asUint8List();
    final maskArrayData = maskImageData.buffer.asUint8List();
    final result = await _imageCutoutPlugin.cutoutImageData(
      originArrayData,
      maskArrayData,
      antiAliasing: _antiAliasing,
      antiRadius: 1,
    );
    debugPrint(
        "===> cut data result : ${result.code} : ${result.success} : ${result.errorMsg} : ${result.result}");
    setState(() {
      _futureImageResult = null;
      _imageResult = result;
    });
    return result;
  }

  Future<ImageResult> _cutoutPathImage() async {
    final result = await _imageCutoutPlugin.cutoutImageFile(
      originFilePath,
      maskFilePath,
      antiAliasing: _antiAliasing,
      antiRadius: 1,
    );
    debugPrint(
        "===> cut file result : ${result.code} : ${result.success} : ${result.errorMsg} : ${result.path}");
    setState(() {
      _futureImageResult = null;
      _imageResult = result;
    });
    return result;
  }

  void _cancel() async {
    final result = await _imageCutoutPlugin.cancel();
    debugPrint("===> cancel result : ${result.code} : ${result.success}");
    setState(() {
      _futureImageResult = null;
      _imageResult = result;
    });
  }

  Widget _buildCutoutResultImage() {
    return FutureBuilder(
      future: _futureImageResult,
      builder: (BuildContext context, AsyncSnapshot<ImageResult> snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const LoadingItem();
        } else if (snapshot.hasData &&
            snapshot.data != null &&
            snapshot.data != null &&
            snapshot.data?.success == true) {
          final imageResult = snapshot.data!;
          if (imageResult.result != null) {
            return ImageItem(imageArray: imageResult.result);
          } else {
            return Column(
              children: [
                ImageItem(imageFilePath: imageResult.path),
                Text(imageResult.path!),
              ],
            );
          }
        } else if (snapshot.hasData &&
            snapshot.data != null &&
            snapshot.data?.code == 2000) {
          return const ErrorItem(
            message: "Cancel",
          );
        } else {
          if (_futureImageResult == null && _imageResult == null) {
            return const PlaceHolderItem();
          } else {
            return const ErrorItem();
          }
        }
      },
    );
  }
}

class PlaceHolderItem extends StatelessWidget {
  const PlaceHolderItem({super.key});

  @override
  Widget build(BuildContext context) {
    return const ImageItem();
  }
}

class LoadingItem extends StatelessWidget {
  const LoadingItem({super.key});

  @override
  Widget build(BuildContext context) {
    return const SizedBox(
      width: 220,
      height: 220,
      child: Center(
        child: CircularProgressIndicator(),
      ),
    );
  }
}

class ErrorItem extends StatelessWidget {
  final String message;

  const ErrorItem({super.key, this.message = "Error"});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 220,
      height: 220,
      child: Center(
        child: Text(
          message,
          style: const TextStyle(color: Colors.red, fontSize: 24),
        ),
      ),
    );
  }
}
