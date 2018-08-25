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

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\cattom\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


#-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#keep all classes that might be used in XML layouts
-keep public class * extends android.view.View
#-keep public class * extends android.app.Fragment
#-keep public class * extends android.support.v4.Fragment

#eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
#eventbus end

#keep all public and protected methods that could be used by java reflection
-keepclassmembernames class * {
    public protected <methods>;
}

-keepclasseswithmembernames class * {
	native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class ** {
    public void onEvent*(**);
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class * extends android.app.Fragment {
   public void *(android.view.View);
}

#按照Google的说明去掉这部分，会导致8.3版本以下不再出admob广告
#-keep public class com.google.android.gms.ads.** {
#   public *;
#}
#
#-keep public class com.google.ads.** {
#   public *;
#}

#gms
#-keep class com.google.android.gms.** { *; }
#-dontwarn com.google.android.gms.**

-assumenosideeffects class com.google.android.gms.ads.internal.ClientApi {
     public static void retainReference(...);
}
#Google广告结束

#-keep public class com.hiblock.systools.** {
#	protected *;
#}

-keep public class blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil.** {
	public *;
}
-keep public class com.md.serverflash.util.LogUtil.** {
	public *;
}
-keep public class com.md.block.util.LogUtil.** {
	public *;
}

-dontwarn **CompatHoneycomb
-dontwarn org.htmlcleaner.*
-dontwarn com.google.ads.**
-dontwarn com.androidquery.**
-dontwarn com.nhaarman.**
-dontwarn com.nineoldandroids.**
-dontwarn android.**
-dontwarn com.facebook.ads.internal.**
-dontwarn com.squareup.picasso.**

#android support v4
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.app.Fragment

#flurry
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotations*, EnclosingMethod

#sasmsung
-keep class com.samsung.android.sdk.** { *;}
-dontwarn com.samsung.android.sdk.**

#Self
-keep class android.content.pm.** {*;}

-renamesourcefileattribute s
-keepattributes SourceFile,LineNumberTable

#Greendao
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

# OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

# For mediation
-keepattributes *Annotation*

# Other required classes for Google Play Services
# Read more at http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}

#Gson
-keepattributes Signature
-keepattributes *Annotations*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

-keep class blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideModelConfig
#-keepclassmembers class blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideModelConfig {<fields>;}
#-keepnames public class blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideModelConfig* {
#     <fields>;
#     <methods>;
#}

# callflashset
-keep class com.md.flashset.CallFlashSet
-keep class com.md.flashset.helper.** {*;}
-keep class com.md.flashset.bean.** {*;}
-keep class com.md.flashset.download.** {*;}
-keep class com.md.flashset.Utils.** {*;}
-keep class com.md.flashset.manager.** {*;}
-keep class com.md.flashset.view.** {*;}
-keep class com.md.flashset.volume.** {*;}
-keepnames class com.md.flashset.R$* {
    public final <fields>;
}

-keep class com.md.flashset.bean.CallFlashInfo
-keepclassmembers class com.md.flashset.bean.CallFlashInfo {<fields>;}
-keepnames public class com.md.flashset.bean.CallFlashInfo* {
     <fields>;
     <methods>;
}

-keep class com.md.flashset.bean.CallFlashInfoBean
-keepclassmembers class com.md.flashset.bean.CallFlashInfoBean {<fields>;}
-keepnames public class com.md.flashset.bean.CallFlashInfoBean* {
      <fields>;
      <methods>;
}
# callflashset end

# serverflash sdk
-keep class com.md.serverflash.ThemeSyncManager
-keep class com.md.serverflash.callback.** {*;}
-keep class com.md.serverflash.beans.** {*;}
-keep class com.md.serverflash.download.** {*;}
-keepnames class com.md.serverflash.R$* {
    public final <fields>;
}

-keep class com.md.serverflash.beans.Category
-keepclassmembers class com.md.serverflash.beans.Category {<fields>;}
-keepnames public class com.md.serverflash.beans.Category* {
      <fields>;
      <methods>;
}
-keep class com.md.serverflash.beans.Theme
-keepclassmembers class com.md.serverflash.beans.Theme {<fields>;}
-keepnames public class com.md.serverflash.beans.Theme* {
      <fields>;
      <methods>;
}
# serverflash sdk end

# app bean
-keep class blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ServerParamBean
-keepclassmembers class blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ServerParamBean {<fields>;}
-keepnames public class blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ServerParamBean$* {
     <fields>;
     <methods>;
}

-keep class blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo
-keepclassmembers class blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo {<fields>;}
-keepnames public class blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo* {
      <fields>;
      <methods>;
}
# app bean end

#LibMarsdaemon
-keep class com.third.marsdaemon.NativeDaemonBase{*;}
-keep class com.third.marsdaemon.nativ.NativeDaemonAPI20{*;}
-keep class com.third.marsdaemon.nativ.NativeDaemonAPI21{*;}
-keep class com.third.marsdaemon.DaemonApplication{*;}
-keep class com.third.marsdaemon.DaemonClient{*;}
-keepattributes Exceptions,InnerClasses,...
-keep class com.third.marsdaemon.DaemonConfigurations{*;}
-keep class com.third.marsdaemon.DaemonConfigurations$*{*;}

# for aquery onclick need param (View v)
# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
# for aquery onclick need param (View v)
-keepclassmembers class * extends android.app.Fragment {
   public void *(android.view.View);
}

#-keep jsoup
-keeppackagenames org.jsoup.nodes
#-dontwarn org.jsoup.**

#Self
#-keep class android.content.pm.** {*;}
-keep class com.android.internal.telephony.** {*;}
-dontwarn com.android.internal.telephony.**


### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
## If you do not use RxJava:
-dontwarn rx.**


# OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#20180114
#-dontwarn com.squareup.okhttp.**
#-dontwarn okio.**
#####
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# libphonenumber
-keep class com.google.i18n.phonenumbers.** { *; }

# block
-keep class com.android.internal.telephony.ITelephony { *; }

# pinyin4j
-dontwarn net.soureceforge.pinyin4j.**
-dontwarn demo.**
-keep class net.sourceforge.pinyin4j.** { *;}
-keep class demo.** { *;}
-keep class com.hp.** { *;}
#-dontwarn net.sourceforge.pinyin4j.**
#-keep class net.sourceforge.pinyin4j.** { *;}
#-keep class net.sourceforge.pinyin4j.format.** { *;}
#-keep class net.sourceforge.pinyin4j.format.exception.** { *;}

# baidu
-keep class com.duapps.ad.**{*;}


-keep class com.mopub.mobileads.BaseWebView
-dontwarn com.mopub.mobileads.BaseWebView
-keep class com.mopub.mraid.MraidController
-dontwarn com.mopub.mraid.MraidController

#-dontwarn com.kingja.loadsir.**
#-keep class com.kingja.loadsir.** {*;}

#lib.kingja.switchbutton.SwitchMultiButton;
#-dontwarn lib.kingja.**;
#-keep class lib.kingja.** {*;}

# jiagu
#-keep class com.nis.**{*;}
#-keep class * extends android.app.Application
#-keep class com.mopub.**{*;}
#-keep class org.json.**{*;}


# sms
#-dontwarn ezvcard.Ezvcard.**
#-dontwarn ezvcard.VCard.**
#-dontwarn com.vdurmont.emoji.**


#-libraryjars libs/android-query.jar
#-libraryjars libs/android-support-v4.jar
#-libraryjars libs/FlurryAnalytics-4.1.0.jar
#-libraryjars libs/FlurryAnalytics-5.5.0.jar
#-libraryjars libs/library-listviewanimations.jar
#-libraryjars libs/nineoldandroids-2.4.0.jar
#-libraryjars lib/layoutlib.jar
#-libraryjars libs/AF-Android-SDK-v2.3.1.12.jar
#-libraryjars libs/AF-Android-SDK-v2.3.1.17.jar
#-libraryjars lib/original-android.jar
#-libraryjars libs/facebook-pc.jar

###cpm
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


# For Smaato
-dontwarn com.smaato.soma.SomaUnityPlugin* -dontwarn com.millennialmedia**
-dontwarn com.facebook.**

# For Mobfox
-keep class com.mobfox.** { *; }
-keep class com.mobfox.adapter.** {*;}
-keep class com.mobfox.sdk.** {*;}

-dontwarn com.mobfox.sdk.webview.MobFoxWebView*
-dontwarn com.mobfox.sdk.webview.MobFoxWebViewClient*
-dontwarn com.mobfox.sdk.utils.ProxyFactory*
-dontwarn com.mobfox.sdk.tags.WaterfallManager*
-dontwarn com.mobfox.sdk.tags.BaseTag*
-dontwarn com.mobfox.sdk.tags.BaseTag*
-dontwarn com.mobfox.sdk.rewardedads.RewardedEvent*
-dontwarn com.mobfox.sdk.interstitialads.InterstitialEvent*
-dontwarn com.mobfox.sdk.interstitialads.InterstitialActivity*
-dontwarn com.mobfox.sdk.dmp.SyncJsonArray*

# For PubNative
-keepattributes Signature
-keep class net.pubnative.** { *; }

# For AOL
-keepclassmembers class com.millennialmedia** { public *; }
-keep class com.millennialmedia**

