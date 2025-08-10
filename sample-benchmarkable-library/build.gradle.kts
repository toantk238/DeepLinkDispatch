plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.manadr.deps)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.airbnb.deeplinkdispatch.sample.benchmarkable"
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