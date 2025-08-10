// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.sonarqube) apply true
}

group = "com.manadr.dependencies"
version = "SNAPSHOT"

dependencies {
    compileOnly(gradleApi())
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.sonarqube.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.compose.compiler.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)

    detektPlugins(libs.detekt.formatting)
}

gradlePlugin {
    plugins {

        create("simplePlugin") {
            id = "manadr.dependencies"
            implementationClass = "MaNaDrBuildPlugin"
        }
    }
}

sonar {
    properties {
        property("sonar.sources", "src/main")
        property("sonar.java.binaries", layout.buildDirectory.file("classes/kotlin/main").get())
        property(
            "sonar.kotlin.detekt.reportPaths",
            layout.buildDirectory.file("reports/detekt/detekt.xml").get()
        )
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.projectBaseDir", project.rootProject.projectDir.toString())
        property("sonar.verbose", true)
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    source.setFrom(files(layout.projectDirectory.dir("src/main")))
    config.setFrom(file(layout.projectDirectory.file("config/detekt-config.yml")))
    autoCorrect = false
    debug = false
    buildUponDefaultConfig = false
    allRules = false
}