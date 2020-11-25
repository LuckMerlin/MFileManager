import 'dart:ffi';
import 'dart:io';


final DynamicLibrary nativeAddLib = Platform.isAndroid
    ? DynamicLibrary.open("liblmplayer.so")
    : DynamicLibrary.process();
//
// final String Function() nativeAdd = nativeAddLib
//     .lookup<NativeFunction<Pointer<Utf8> Function()>>("getVersion").asFunction();

