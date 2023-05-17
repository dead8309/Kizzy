#OkHttp Rules
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

#Gson Rules
 -keepattributes Signature
 -keepattributes *Annotation*
 -dontwarn sun.misc.**

 # JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
 -keep class * extends com.google.gson.TypeAdapter
 -keep class * implements com.google.gson.TypeAdapterFactory
 -keep class * implements com.google.gson.JsonSerializer
 -keep class * implements com.google.gson.JsonDeserializer

 #Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
 -keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
 -keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-keepclassmembers,allowobfuscation class * {

  @com.google.gson.annotations.SerializedName <fields>;

}

# Keep Domain data classes
-keep class com.my.kizzy.domain.model.** { <fields>; }

# Keep Data data classes
-keep class com.my.kizzy.data.remote.** { <fields>; }

# Keep Gateway data classes
-keep class kizzy.gateway.entities.** { <fields>; }

# slf4j error during build
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Waiting for new retrofit release to remove these rules
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation