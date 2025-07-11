plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

// This is just here to show an example how DLD works with either `annotationProcessor` (sample-library)
// 'kapt' (this lib) or 'ksp' (the main sample app module) and even can mix and match those between
// modules.

android {
    namespace = "com.airbnb.deeplinkdispatch.sample.kaptlibrary"
    compileSdk = 35

    defaultConfig {
        minSdk = 16
        targetSdk = 35

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "deepLink.incremental" to "true",
                    "deepLink.customAnnotations" to "com.airbnb.deeplinkdispatch.sample.library.LibraryDeepLink"
                )
            }
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":deeplinkdispatch"))
    kapt(project(":deeplinkdispatch-processor"))
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit.junit)
}