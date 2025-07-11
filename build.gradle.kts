// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath(libs.androidx.benchmark.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinter) apply false
    id("com.vanniktech.maven.publish") version "0.22.0" apply false
}

// Repositories are configured via settings.gradle.kts

// Configure Java toolchain for all projects
subprojects {
    afterEvaluate {
        if (project.hasProperty("java")) {
            configure<JavaPluginExtension> {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(17))
                }
            }
        }
        if (project.hasProperty("android")) {
            configure<com.android.build.gradle.BaseExtension> {
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = "17"
    }
}

fun getReleaseRepositoryUrl(): String {
    return if (hasProperty("RELEASE_REPOSITORY_URL")) {
        property("RELEASE_REPOSITORY_URL") as String
    } else {
        "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    }
}

fun getSnapshotRepositoryUrl(): String {
    return if (hasProperty("SNAPSHOT_REPOSITORY_URL")) {
        property("SNAPSHOT_REPOSITORY_URL") as String
    } else {
        "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}