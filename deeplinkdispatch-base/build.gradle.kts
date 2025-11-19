plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    id("checkstyle")
    alias(libs.plugins.manadr.deps)
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

dependencies {
    implementation(libs.squareup.okio)
    implementation(libs.single.findBugs)
    implementation(libs.androidx.annotations)
    testImplementation(libs.junit.junit)
    testImplementation(libs.assertj.core)
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    isShowViolations = true
    configProperties = mapOf("checkstyle.cache.file" to rootProject.file("build/checkstyle.cache"))
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
        artifactId = "deeplinkdispatch-base",
    )
}