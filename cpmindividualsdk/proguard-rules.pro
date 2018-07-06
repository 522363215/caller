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

#IA使用AAR包中的混淆，无需单独处理 #这一部分需要跟现有混淆核对，如果没有就加上 -dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
   public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class android.support.v7.** {
    public protected *;
}

-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
-dontwarn javax.xml.**   # AerServ SDK
-keep class com.aerserv.** { *; }
-keepclassmembers class com.aerserv.** { *; }

# Moat
-keep class com.moat.** { *; }
-dontwarn com.moat.**

# For Adcolony AS其他相关渠道
-dontwarn android.webkit.**
-dontwarn com.adcolony.**
-keep class com.adcolony.sdk.* { *; }

# For Admob
-keep class com.google.android.gms.ads.** { *; }

# For Applovin
-dontwarn com.applovin.**
-keep class com.applovin.** { *; }

# For AppNext
-keep class com.appnext.** { *; }
-dontwarn com.appnext.**

# For Chartboost
-dontwarn com.chartboost.sdk.**
-keep class com.chartboost.sdk.** { *; }

# For Facebook
-dontwarn com.facebook.ads.**
-keep class com.facebook.ads.** { *; }

# For Millennial Media
-dontwarn com.millennialmedia.**
-keep class com.millennialmedia.** { *; }

# For Mopub
-dontwarn com.mopub.**
-keep class com.mopub.** { *; }

# For myTarget
-dontwarn com.my.target.**
-keep class com.my.target.** { *; }

# For RhythmOne
-dontwarn com.rhythmone.ad.sdk.**
-keep class com.rhythmone.ad.sdk.** { *; }

# For Tremor
-dontwarn com.tremorvideo.sdk.**
-keep class com.tremorvideo.sdk.android.videoad.** { *; }

# For Unity Ads
-dontwarn com.unity3d.ads.**
-keep class com.unity3d.ads.** { *; }

# For Vungle
-dontwarn com.vungle.publisher.**
-keep class com.vungle.publisher.** { *; }

# For Flurry by Yahoo
-dontwarn android.support.customtabs.**
-dontwarn com.flurry.android.**
-dontwarn com.inmobi.**
-keep class com.flurry.** { *; }