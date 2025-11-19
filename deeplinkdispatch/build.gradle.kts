plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.manadr.deps)
    id("checkstyle")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
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

publishing {
    repositories {
        mavenLocal()
    }
}
mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "com.airbnb",
        artifactId = "deeplinkdispatch",
    )
}
