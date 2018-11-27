
#清除Log
#-assumenosideeffects class android.util.Log {
#	public static boolean isLoggable(java.lang.String, int);
#	public static int v(...);
#	public static int i(...);
#	public static int w(...);
#	public static int d(...);
#	public static int e(...);
#}

-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*

-dontwarn com.google.**
-keep class com.google.** { *; }

-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewBinder { *; }

-keep class * implements com.bumptech.glide.module.GlideModule
-keepnames class * extends android.support.v4.app.Fragment

-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn rx.**
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
	long producerIndex;
	long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
	rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
	rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontwarn com.baidu.**
-keep class com.baidu.** { *; }

-dontwarn com.amap.**
-keep class com.amap.** { *; }

-keep class com.tencent.map.geolocation.**{*;}
-keep class com.tencent.tencentmap.lbssdk.service.**{*;}

-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**
-keep class c.t.**{*;}
-keepclassmembers class ** { public void on*Event(...); }

-keep class chihane.jdaddressselector.** { *; }
-keep class com.raizlabs.** { *; }

-dontwarn  com.uzmap.pkg.**
-keep class  com.uzmap.pkg.** { *; }
-keep class  com.apicloud.** { *; }

-dontwarn  com.tencent.***
-keep class  com.tencent.** { *; }