# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontshrink
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify
-verbose
-ignorewarnings

-keepattributes Signature
-keepattributes *Annotation*

-keepattributes InnerClasses,Signature,SourceFile,Exceptions,LineNumberTable,*Annotation*

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class **.R$* {
 public static <fields>;
}


##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#不混淆序列化的类
-keep public class * implements java.io.Serializable {*;}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#CSDK
-keep class com.csdk.api.common.** { *; }
#-keep class com.csdk.unui.** { *; }
#-keep class com.csdk.api.ui.** { *; }
-keep class com.csdk.server.activity.** { *; }

#
-dontwarn com.csdk.api.**
-keep class com.csdk.api.core.** { *; }
-keep class com.csdk.api.bean.** { *; }
-dontwarn androidx.databinding.**
-keep class androidx.databinding.** { *; }
-keep class * extends androidx.databinding.DataBinderMapper { *; }
-keep public class * implements java.io.Serializable {*;}

#GME
-dontwarn com.tencent.**
-keep class com.tencent.** { *;}
-keepclassmembers class com.tencent.**{*;}