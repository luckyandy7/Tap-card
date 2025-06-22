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

# 기본 Android 보존 규칙
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 모든 내부 클래스 보존
-keep class com.example.myapplication.** { *; }
-keepclassmembers class com.example.myapplication.** { *; }

# 특히 문제가 되는 내부 클래스들 강력 보존
-keep class com.example.myapplication.CardCustomizeActivity$* { *; }
-keep class com.example.myapplication.InfoInputActivity$* { *; }

# 리스너 인터페이스 보존
-keep class * implements android.view.View$OnClickListener { *; }
-keep class * implements android.widget.SeekBar$OnSeekBarChangeListener { *; }
-keep class * implements android.widget.AdapterView$OnItemSelectedListener { *; }
-keep class * implements android.text.TextWatcher { *; }
-keep class * implements com.google.android.material.slider.Slider$OnChangeListener { *; }

# 익명 클래스 보존
-keepclassmembers class * {
    private void lambda*(...);
}

# 생성자 보존
-keepclassmembers class * {
    public <init>(...);
}

# 모든 public 메서드 보존
-keepclassmembers class * {
    public *;
}

# 모든 내부 클래스의 필드와 메서드 보존
-keepclassmembers class *$* {
    *;
}

# Serializable 클래스 보존
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Material Design 관련 보존
-keep class com.google.android.material.** { *; }

# Firebase 관련 보존
-keep class com.google.firebase.** { *; }

# 모든 어노테이션 보존
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# 모든 enum 보존
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# NFC 관련 보존
-keep class android.nfc.** { *; }

# Glide 관련 보존
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# ZXing QR 코드 관련 보존
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }

# 디버그 정보 보존
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
