plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.manadr.deps)
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

apply(plugin = "androidx.benchmark")

android {
    namespace = "com.airbnb.deeplinkdispatch.sample.benchmark"
    setupAndroidBasicConfigs()

    defaultConfig {
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }

    buildTypes {
        debug {
            // Since debuggable can't be modified by gradle for library modules,
            // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "benchmark-proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Add your dependencies here. Note that you cannot benchmark code
    // in an app module this way - you will need to move any code you
    // want to benchmark to a library module:
    // https://developer.android.com/studio/projects/android-library#Convert
    implementation(project(":sample-benchmarkable-library"))
    implementation(project(":deeplinkdispatch"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintLayout)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.androidx.benchmark.junit4)
}

kotlin { autoConfig() }
setupCompileTask()