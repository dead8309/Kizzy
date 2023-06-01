#OkHttp Rules
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Ktor Rules
-keepclassmembers class io.ktor.http.** { *; }

# Keep Domain data classes
-keep class com.my.kizzy.domain.model.** { <fields>; }

# Keep Data data classes
-keep class com.my.kizzy.data.remote.** { <fields>; }

# Keep Gateway data classes
-keep class kizzy.gateway.entities.** { <fields>; }

# slf4j error during build
-dontwarn org.slf4j.impl.StaticLoggerBinder

# some unknown error
-dontwarn java.lang.invoke.StringConcatFactory

-dontwarn com.my.kizzy.resources.R$drawable