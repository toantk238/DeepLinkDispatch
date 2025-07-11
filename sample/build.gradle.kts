plugins {
    alias(libs.plugins.android.application)
    id("checkstyle")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    isShowViolations = true
    configProperties = mapOf("checkstyle.cache.file" to rootProject.file("build/checkstyle.cache"))
}

android {
    namespace = "com.airbnb.deeplinkdispatch.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.airbnb.deeplinkdispatch.sample"
        minSdk = 16
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    packagingOptions {
        resources {
            excludes += "META-INF/services/javax.annotation.processing.Processor"
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    
    lint {
        disable.add("InvalidPackage")
    }
}

dependencies {
    implementation(project(":deeplinkdispatch"))
    ksp(project(":deeplinkdispatch-processor"))
    implementation(project(":sample-library"))
    implementation(project(":sample-kapt-library"))
    implementation(project(":sample-benchmarkable-library"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.localbroadcastmanager)

    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.junit.junit)
    testImplementation(libs.mockito.mockk)
}

ksp {
    arg("deepLinkDoc.output", "${layout.buildDirectory.get()}/doc/deeplinks.txt")
    arg("deepLink.incremental", "true")
    arg("deepLink.customAnnotations",
        "com.airbnb.deeplinkdispatch.sample.AppDeepLink|" +
            "com.airbnb.deeplinkdispatch.sample.WebDeepLink|" +
            "com.airbnb.deeplinkdispatch.sample.WebPlaceholderDeepLink|" +
            "com.airbnb.deeplinkdispatch.sample.library.LibraryDeepLink"
    )
}