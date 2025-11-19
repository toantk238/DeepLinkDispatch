plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.manadr.deps)
    id("checkstyle")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
    // alias(libs.plugins.kotlinter)  // Disabled due to compatibility issues
}

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
    testImplementation(libs.androidx.room.compiler.processing.testing)
    // For test compile we need a reference of the DeepLinkDelegate (which has android dependencies)
    // Cannot depend on them from Maven as they are .aar and not .jar files (this is a java project)
    testImplementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf(
                    "androidx.localbroadcastmanager-1.0.0-beta1.jar",
                    "androidx.core-1.0.0-beta1.jar"
                )
            )
        )
    )
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

tasks.test {
    // Add JVM arguments for Java 17 compatibility with KAPT
    jvmArgs(
        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    )
}

publishing {
    repositories {
        mavenLocal()
    }
}
mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "com.airbnb",
        artifactId = "deeplinkdispatch-processor",
    )
}
