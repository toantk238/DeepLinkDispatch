plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.manadr.deps)
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

// This is just here to show an example how DLD works with either `annotationProcessor` (sample-library)
// 'kapt' (this lib) or 'ksp' (the main sample app module) and even can mix and match those between
// modules.

android {
    namespace = "com.airbnb.deeplinkdispatch.sample.kaptlibrary"
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
    kapt(project(":deeplinkdispatch-processor"))
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit.junit)
}

kotlin { autoConfig() }
setupCompileTask()