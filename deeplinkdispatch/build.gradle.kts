plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
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
    namespace = "com.airbnb.android.deeplinkdispatch"
    compileSdk = 35

    defaultConfig {
        minSdk = 16
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("proguard-rules.pro")
    }
    
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}