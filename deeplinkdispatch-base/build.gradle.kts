plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    id("checkstyle")
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

apply(from = "../publishing.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_17
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