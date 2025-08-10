plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.manadr.deps)
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

android {
    namespace = "com.airbnb.deeplinkdispatch.sample.library"
    setupAndroidBasicConfigs()

    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "deepLink.incremental" to "true",
                    "deepLink.customAnnotations" to "com.airbnb.deeplinkdispatch.sample.library.LibraryDeepLink"
                )
            }
        }
    }
}

dependencies {
    implementation(project(":deeplinkdispatch"))
    annotationProcessor(project(":deeplinkdispatch-processor"))
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit.junit)
}

kotlin { autoConfig() }
setupCompileTask()