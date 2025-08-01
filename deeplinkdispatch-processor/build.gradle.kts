plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.manadr.deps)
    id("checkstyle")
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

apply(from = "../publishing.gradle")

dependencies {
    implementation(project(":deeplinkdispatch-base"))
    implementation(libs.single.findBugs)
    implementation(libs.squareup.javapoet)
    implementation(libs.androidx.annotations)
    implementation(libs.ksp.symbolProcessing)
    implementation(libs.androidx.room.compiler.processing)

    testImplementation(libs.junit.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.google.android)
    // For test compile we need a reference of the DeepLinkDelegate (which has android dependencies)
    // Cannot depend on them from Maven as they are .aar and not .jar files (this is a java project)
    testImplementation(fileTree(mapOf("dir" to "libs", "include" to listOf("androidx.localbroadcastmanager-1.0.0-beta1.jar", "androidx.core-1.0.0-beta1.jar"))))
    testImplementation(libs.kotlin.compile.testing)
    testImplementation(libs.kotlin.compile.testing.ksp)
    testImplementation(libs.mockito.mockk)
    testImplementation(libs.kotlin.reflect)
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    isShowViolations = true
    configProperties = mapOf("checkstyle.cache.file" to rootProject.file("build/checkstyle.cache"))
}

kotlin { autoConfig() }
setupCompileTask()