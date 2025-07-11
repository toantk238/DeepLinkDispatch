plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.airbnb.deeplinkdispatch.sample.benchmarkable"
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
}

dependencies {
    implementation(project(":deeplinkdispatch"))
    annotationProcessor(project(":deeplinkdispatch-processor"))
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit.junit)
}