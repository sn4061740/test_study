-keepattributes *Annotation* 
-dontwarn com.tencent.tinker.anno.AnnotationProcessor 
-keep @com.tencent.tinker.anno.DefaultLifeCycle public class *
-keep public class * extends android.app.Application {
    *;
}

-keep public class com.tencent.tinker.entry.ApplicationLifeCycle {
    *;
}
-keep public class * implements com.tencent.tinker.entry.ApplicationLifeCycle {
    *;
}

-keep public class com.tencent.tinker.loader.TinkerLoader {
    *;
}
-keep public class * extends com.tencent.tinker.loader.TinkerLoader {
    *;
}
-keep public class com.tencent.tinker.loader.TinkerTestDexLoad {
    *;
}
-keep public class com.tencent.tinker.loader.TinkerTestAndroidNClassLoader {
    *;
}

#your dex.loader patterns here
-keep class com.xcore.MainApplication
-keep class com.tencent.tinker.loader.**
