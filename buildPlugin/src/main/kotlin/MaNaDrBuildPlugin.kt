import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.Lint
import com.android.build.api.dsl.VariantDimension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.TestExtension
import obj.Bundle
import obj.BundleData
import obj.ProjectConfig
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.fileTree
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.com.google.gson.Gson
import org.jetbrains.kotlin.com.google.gson.GsonBuilder
import org.jetbrains.kotlin.com.google.gson.JsonElement
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.util.Properties

lateinit var globalJavaVersion: String

lateinit var detektVersion: String

private const val envFilePath = "src/main/assets/env/all_env.json"

lateinit var versionCatalog: VersionCatalog

class MaNaDrBuildPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        versionCatalog = target.findVersionCatalog("libs")
        versionCatalog.run {
            globalJavaVersion = requiredVersion("java")
            detektVersion = requiredVersion("detekt")
        }

        target.tasks.withType<KaptWithoutKotlincTask>()
            .configureEach {
                listOf(
                    "util",
                    "file",
                    "main",
                    "jvm",
                    "processing",
                    "comp",
                    "tree",
                    "api",
                    "parser",
                    "code"
                ).flatMap {
                    listOf(
                        "--add-opens",
                        "jdk.compiler/com.sun.tools.javac.$it=ALL-UNNAMED"
                    )
                }.forEach(kaptProcessJvmArgs::addAll)
            }
    }
}

fun Project.getEnvMap(): Map<String, Bundle> {
    val envFile = file(envFilePath).readText()
    val bundleData = Gson().fromJson(envFile, BundleData::class.java)!!
    return bundleData.data.associateBy { it.prefix ?: "" }
}

object AndroidBuildDeps {

    const val MIN_SDK = 26

    const val TARGET_SDK = 35

    const val COMPILE_SDK = 35

    const val BUILD_TOOLS = "35.0.1"
}

fun DependencyHandlerScope.daggerCompiler() {
    "ksp"(versionCatalog.takeLib("dagger.compiler"))
    "ksp"(versionCatalog.takeLib("dagger.android.processor"))
}

fun DependencyHandlerScope.setUpTestDeps() {
    unitTestDeps()
    "androidTestImplementation"(versionCatalog.takeLib("androidx.annotations"))
    "androidTestImplementation"(versionCatalog.takeLib("androidx.test.runner"))
    "androidTestImplementation"(versionCatalog.takeLib("androidx.test.espresso.core"))
    "androidTestImplementation"(versionCatalog.takeLib("androidx.test.ext"))
    "androidTestImplementation"(versionCatalog.takeLib("androidx.test.core.ktx"))
}

val benchmarkDeps by lazy {
    listOf(
        "androidx.benchmark.macro",
        "androidx-test-ext",
        "androidx-test-espresso-core",
        "androidx-test-uiautomator",
        "tracing-perfetto",
        "tracing-perfetto-binary",
    ).map {
        versionCatalog.takeLib(it)
    }
}

val unitTestLibs by lazy {
    listOf(
        versionCatalog.takeLib("junit.junit"),
        versionCatalog.takeLib("androidx.test.core.ktx"),
        versionCatalog.takeLib("mockito.core"),
        versionCatalog.takeLib("mockito.inline"),
        versionCatalog.takeLib("mockito.kotlin"),
        versionCatalog.takeLib("mockito.mockk"),
        versionCatalog.takeLib("androidx.arch.core.testing"),
        versionCatalog.takeLib("kotlinx.coroutines.test")
    )
}

fun DependencyHandlerScope.unitTestDeps() {
    unitTestLibs.forEach {
        "testImplementation"(it)
    }
}

object DebugDeps {

    val RECOMMEND_LIBS = listOf(
        "fb.soloader",
        "flipper.core",
        "flipper.network",
        "flipper.leakCanary",
        "single.chuck",
        "single.leakCanary",
        "single.takt"
    ).map { versionCatalog.takeLib(it) }

    val HYPERION_LIBS = listOf(
        "hyperion.core",
        "hyperion.crash",
        "hyperion.disk",
        "hyperion.measurement",
        "hyperion.phoenix",
        "hyperion.recorder",
        "hyperion.shared.preferences",
        "hyperion.font.scale",
        "hyperion.chucker",
        "hyperion.app.info",
    ).map { versionCatalog.takeLib(it) }
}

fun DependencyHandlerScope.setupFirebase() {
    "implementation"(platform(FirebaseDeps.BOM))
    "implementation"(FirebaseDeps.CRASHLYTICS)
    "implementation"(FirebaseDeps.PERFORMANCE)
}

fun DependencyHandlerScope.implementations(vararg libs: Any) {
    libs.forEach { "implementation"(it) }
}

fun DependencyHandlerScope.airBnbDeepLinkDispatchCompiler() {
    "ksp"(versionCatalog.takeLib("airbnbDeepLink.processor"))
}

object SingleLibs {

    const val CROP_IMAGE = "com.soundcloud.android:android-crop:1.0.1@aar"

    const val APMEM_FLOW_LAYOUT = "org.apmem.tools:layouts:1.10@aar"

    const val ADVANCED_RECYCLER_VIEW =
        "com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:1.0.0@aar"

    const val QR_CODE_GENERATOR = "com.bottlerocketstudios:barcode:1.0.3@aar"

}

object FirebaseDeps {

    val BOM: MinimalExternalModuleDependency by lazy { versionCatalog.takeLib("firebase.bom") }

    const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics"

    const val PERFORMANCE = "com.google.firebase:firebase-perf-ktx"

}

fun DependencyHandlerScope.jetpackCompose() {
    val composeBom = platform(versionCatalog.takeLib("androidx.compose.bom"))
    "implementation"(composeBom)
    "androidTestImplementation"(composeBom)
    "implementation"("androidx.compose.material3:material3")
    "implementation"("androidx.compose.ui:ui-tooling-preview")
    "implementation"("androidx.compose.ui:ui-viewbinding")
    "debugImplementation"("androidx.compose.ui:ui-tooling")
    "androidTestImplementation"("androidx.compose.ui:ui-test-junit4")
    "debugImplementation"("androidx.compose.ui:ui-test-manifest")
    "implementation"("androidx.compose.runtime:runtime-livedata")
    implementations(
        versionCatalog.takeLib("accompanist.swipeRefresh"),
        versionCatalog.takeLib("androidx.navigation.compose"),
        versionCatalog.takeLib("accompanist.appCompatTheme"),
        versionCatalog.takeLib("coil.kt.compose"),
        versionCatalog.takeLib("coil.network.okhttp"),
        versionCatalog.takeLib("androidx.compose.constraintlayout"),
        versionCatalog.takeLib("androidx.compose.foundation"),
        versionCatalog.takeLib("androidx.activity.compose"),
        versionCatalog.takeLib("androidx.lifecycle.viewModelCompose"),
        versionCatalog.takeLib("androidx.lifecycle.runtimeCompose"),
        versionCatalog.takeLib("accompanist.systemConfig"),
    )
}

fun DependencyHandlerScope.composePager() {
    implementations(
        versionCatalog.takeLib("accompanist.pager"),
        versionCatalog.takeLib("accompanist.pagerIndicators"),
    )
}

fun VariantDimension.addStringConstant(key: String, value: String) {
    val finalValue = if (value.startsWith("\"")) {
        value.substringAfter("\"").substringAfterLast("\"")
    } else value
    addManifestPlaceholders(mapOf(key to finalValue))
    resValue("string", key, "\"$finalValue\"")
}

fun VariantDimension.addStringConfigField(key: String, value: String?) {
    val tempValue = value ?: ""
    val finalValue = if (tempValue.startsWith("\"")) {
        tempValue.substringAfter("\"").substringAfterLast("\"")
    } else tempValue
    resValue("string", key, "\"$finalValue\"")
}

fun VariantDimension.addIntConfigField(key: String, value: Int?) {
    value ?: return
    resValue("integer", key, value.toString())
}

fun VariantDimension.addBooleanRes(key: String, value: Boolean) {
    resValue("bool", key, value.toString())
}

fun BaseExtension.setupAndroidBasicConfigs(javaVersion: String = globalJavaVersion) {

    compileSdkVersion(AndroidBuildDeps.COMPILE_SDK)
    buildToolsVersion(AndroidBuildDeps.BUILD_TOOLS)

    defaultConfig {
        minSdk = AndroidBuildDeps.MIN_SDK
        targetSdk = AndroidBuildDeps.TARGET_SDK
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    if (this is CommonExtension<*, *, *, *, *, *>) {
        buildFeatures {
            viewBinding = true
        }

        lint {
            abortOnError = false
            checkGeneratedSources = true
            disable.addAll(listOf("VectorPath", "CoroutineCreationDuringComposition"))
        }
    }

    customKotlinOptions {
        jvmTarget.value(JvmTarget.fromTarget(javaVersion))
    }
}

fun BaseExtension.enableCompose() {

    if (this is CommonExtension<*, *, *, *, *, *>) {
        buildFeatures {
            compose = true
        }
    }
}

fun BaseExtension.setupConsumeProguardFiles(project: Project) {

    buildTypes {

        val pgFiles =
            project.fileTree("dir" to "proguards", "include" to "*.pro").files.toTypedArray()
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles(*pgFiles)
            proguardFiles(*pgFiles)
        }
    }
}

fun BaseExtension.customKotlinOptions(configure: KotlinJvmCompilerOptions.() -> Unit) {
    val kotlinOptions = (this as ExtensionAware).extensions.findByName("compilerOptions")
    if (kotlinOptions !is KotlinJvmCompilerOptions) return
    configure.invoke(kotlinOptions)
}

fun BaseExtension.setupMacroBenchmark() {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (this is TestExtension) {
        // Enable the benchmark to run separately from the app process
        experimentalProperties["android.experimental.self-instrumenting"] = true
    }
}

fun KotlinSingleTargetExtension<*>.configJvm(javaVersion: String = globalJavaVersion) {
    jvmToolchain(javaVersion.toInt())
}

fun KotlinSingleTargetExtension<*>.autoConfig(javaVersion: String = globalJavaVersion) {
    jvmToolchain(javaVersion.toInt())
    sourceSets.getByName("main") {
        dependencies {
            implementation(versionCatalog.takeLib("kotlinx.collections.immutable"))
        }
    }
}

fun DependencyHandlerScope.applyDetektPlugins() {
    "detektPlugins".invoke(versionCatalog.takeLib("detekt.formatting"))
    "detektPlugins".invoke(versionCatalog.takeLib("compose.rules.detekt"))
}

/**
 * https://github.com/facebook/fresco/issues/2598
 */
fun DependencyHandlerScope.suppressFrescoWarning() {
    "compileOnly"(versionCatalog.takeLib("fb.infer.annotation"))
}

fun DependencyHandlerScope.supportApi25() {
    "coreLibraryDesugaring"(versionCatalog.takeLib("android.desugarJdkLibs"))
}

fun Project.setupCompileTask(javaVersion: String = globalJavaVersion) {
    tasks.withType(KotlinCompile::class.java) {
        compilerOptions {
            jvmTarget.value(JvmTarget.fromTarget(javaVersion))
            freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn")
        }
    }

    tasks.withType(JavaCompile::class.java).configureEach {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

fun Project.getPropertiesFile(path: String): Properties? {
    val localPropertiesFile: File = file(path)
    return if (localPropertiesFile.exists()) {
        val stream = localPropertiesFile.inputStream()
        val temp = Properties().apply { load(stream) }
        stream.close()
        temp
    } else null
}

fun Project.findVersionCatalog(name: String): VersionCatalog {
    return extensions.findByType(VersionCatalogsExtension::class.java)!!.find(name).get()
}

fun VersionCatalog.requiredVersion(name: String): String {
    return findVersion(name).get().requiredVersion
}

fun VersionCatalog.takeLib(name: String): MinimalExternalModuleDependency {
    return findLibrary(name).get().get()
}

fun Project.registerTaskGenK8sFile(): TaskProvider<Task> = this.tasks.register("gen_api_env_file") {
    val url = URI("https://falcon-api.manadrdev.com/api/v1/k8s/bundles/").toURL()
    val conn = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
    }
    val bufIn = BufferedReader(InputStreamReader(conn.inputStream))
    val response = StringBuffer()
    var strCurrentLine: String?
    while (bufIn.readLine().also { strCurrentLine = it } != null) {
        response.append(strCurrentLine)
    }
    bufIn.close()

    val gson = GsonBuilder().setPrettyPrinting().create()
    val jObj = gson.fromJson(response.toString(), JsonElement::class.java)

    val envFilePath = project.file(envFilePath)
    val output = gson.toJson(jObj)
    project.file(envFilePath).writeText(output)
}

fun androidxCoreDeps(): List<MinimalExternalModuleDependency> {
    val libs = listOf(
        "androidx.appcompat",
        "androidx.activity.activity",
        "androidx.fragment.fragment"
    )
    return libs.map {
        versionCatalog.findLibrary(it).get().get()
    }
}

fun getProjectModules(moduleFile: File): List<ProjectConfig> {
    return if (!moduleFile.exists()) emptyList()
    else moduleFile.readLines().map { it.trim() }.filter {
        it.isNotEmpty()
    }.map {
        val blocks = it.split(";")
        val name = blocks[0].trim()
        val notateTag = "depNotate"
        val depNotate = blocks.firstOrNull { it.contains(notateTag) }?.let {
            it.substring(notateTag.length + 1, it.length)
        } ?: "implementation"
        ProjectConfig(name = name, depNotation = depNotate, hasLint = !it.contains("no-lint"))
    }
}

fun Lint.disableCommonErrors() = listOf(
    "Instantiatable", "NullSafeMutableLiveData", "SuspiciousModifierThen",
    "Overdraw", "MissingPrefix"
).let {
    disable.addAll(it)
}

/**
 * Enable compose compiler report.
 *
 * **Note** : the report is generated in the build directory. If you run *clean* task, the report will be deleted, then they are not generated in next time because the tasks cache.
 * To be able to see the reports again, you should pass "--rerun-tasks" to the gradle command.
 */
fun ComposeCompilerGradlePluginExtension.enableReport(project: Project) {
    getProjectModules(File("")).filter { it.hasLint }.map { ":${it.name}" }
    if (System.getenv("ENABLE_COMPOSE_REPORT") != "true") return

    reportsDestination.value(project.layout.buildDirectory.dir("compose_compiler"))
    metricsDestination.value(project.layout.buildDirectory.dir("compose_compiler"))
}
