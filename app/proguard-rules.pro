-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}


-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder



#-dontwarn it.fast4x.compose.persist.PersistKt
#-dontwarn it.fast4x.compose.persist.PersistMapCleanupKt
#-dontwarn it.fast4x.compose.persist.PersistMapOwner
#-dontwarn it.fast4x.compose.persist.UtilsKt
#-dontwarn it.fast4x.compose.reordering.DraggedItemKt
#-dontwarn it.fast4x.compose.reordering.ReorderKt
#-dontwarn it.fast4x.compose.reordering.ReorderingState
#-dontwarn it.fast4x.compose.reordering.ReorderingStateKt
#-dontwarn it.fast4x.compose.routing.Route0
#-dontwarn it.fast4x.compose.routing.Route1
#-dontwarn it.fast4x.compose.routing.Route3
#-dontwarn it.fast4x.compose.routing.Route
#-dontwarn it.fast4x.compose.routing.RouteHandlerKt
#-dontwarn it.fast4x.compose.routing.RouteHandlerScope
#-dontwarn it.fast4x.compose.routing.TransitionsKt