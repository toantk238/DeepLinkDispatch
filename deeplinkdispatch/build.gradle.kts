plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.manadr.deps)
    id("checkstyle")
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

apply(from = "../publishing.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(project(":deeplinkdispatch-base"))
    implementation(libs.squareup.okio)
    implementation(libs.single.findBugs)
    implementation(libs.androidx.localbroadcastmanager)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.mockito.mockk)
    testImplementation(libs.junit.junit)
    testImplementation(libs.assertj.core)
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    isShowViolations = true
    configProperties = mapOf("checkstyle.cache.file" to rootProject.file("build/checkstyle.cache"))
}

android {
    
    setupAndroidBasicConfigs()
    namespace = "com.airbnb.android.deeplinkdispatch"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("proguard-rules.pro")
    }
    
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin { autoConfig() }
setupCompileTask()