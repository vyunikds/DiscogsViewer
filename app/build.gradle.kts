import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    kotlin("plugin.serialization")
    `maven-publish`
}

open class AppPublishingExtension {
    var group: String = "com.example.discogsviewer"
    var artifactId: String = "discogsviewer-app"
    var version: String = "1.0"
    var repositoryDir: File = File("maven-local")
}

val appPublish = AppPublishingExtension().also {
    it.repositoryDir = layout.buildDirectory.dir("maven-local").get().asFile
    extensions.add("appPublish", it)
}

appPublish.apply {
    version = android.defaultConfig.versionName ?: version
}

android {
    namespace = "com.example.discogsviewer"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.discogsviewer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.discogsviewer.DiscogsTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

abstract class PublishApkTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val releaseApkDir: DirectoryProperty

    @get:Input
    abstract val mavenGroup: Property<String>

    @get:Input
    abstract val artifactId: Property<String>

    @get:Input
    abstract val version: Property<String>

    @get:OutputDirectory
    abstract val repositoryDir: DirectoryProperty

    @TaskAction
    fun publish() {
        val group = mavenGroup.get()
        val artifactId = artifactId.get()
        val version = version.get()
        val repo = repositoryDir.get().asFile
        val apkFile = releaseApkDir.get().asFile
            .listFiles { file -> file.isFile && file.name.startsWith("app-release") && file.name.endsWith(".apk") }
            ?.firstOrNull()
            ?: throw GradleException("No release APK found in ${releaseApkDir.get().asFile}. Build it first (assembleRelease).")

        val versionDir = File(File(File(repo, group.replace('.', File.separatorChar)), artifactId), version)
        versionDir.mkdirs()

        apkFile.copyTo(File(versionDir, "$artifactId-$version.apk"), overwrite = true)
        File(versionDir, "$artifactId-$version.pom").writeText(pom(group, artifactId, version))
        writeMavenMetadata(repo, group, artifactId, version)

        logger.lifecycle("Published $group:$artifactId:$version to ${repo.absolutePath}")
    }

    private fun pom(group: String, artifactId: String, version: String): String = """
        <?xml version="1.0" encoding="UTF-8"?>
        <project xmlns="http://maven.apache.org/POM/4.0.0"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
            <modelVersion>4.0.0</modelVersion>
            <groupId>${group.xmlEscaped()}</groupId>
            <artifactId>${artifactId.xmlEscaped()}</artifactId>
            <version>${version.xmlEscaped()}</version>
            <name>DiscogsViewer</name>
            <description>DiscogsViewer Android application (APK)</description>
        </project>
    """.trimIndent()

    private fun writeMavenMetadata(repo: File, group: String, artifactId: String, version: String) {
        val artifactDir = File(File(repo, group.replace('.', File.separatorChar)), artifactId)
        val versions = (artifactDir.listFiles { file -> file.isDirectory }?.map { file -> file.name } ?: emptyList())
            .ifEmpty { listOf(version) }
        val latest = versions.max()
        val lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val versionsXml = versions.joinToString("") { "<version>$it</version>" }
        val metadata = """
            <?xml version="1.0" encoding="UTF-8"?>
            <metadata>
                <groupId>${group.xmlEscaped()}</groupId>
                <artifactId>${artifactId.xmlEscaped()}</artifactId>
                <versioning>
                    <latest>$latest</latest>
                    <release>$latest</release>
                    <versions>$versionsXml</versions>
                    <lastUpdated>$lastUpdated</lastUpdated>
                </versioning>
            </metadata>
        """.trimIndent()
        File(artifactDir, "maven-metadata.xml").writeText(metadata)
    }

    private fun String.xmlEscaped(): String =
        replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
}

dependencies {
    implementation(project(":feature:releases"))
    implementation(project(":feature:search"))
    implementation(project(":feature:favorites"))
    implementation(project(":feature:details"))
    implementation(project(":feature:settings"))
    implementation(project(":core:basepresentation"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":data:settings"))

    implementation(libs.bundles.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.hilt.core)
    kapt(libs.hilt.compiler)

    implementation(libs.hilt.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlin.test.junit)
    androidTestImplementation(project(":core:database"))
    androidTestImplementation(project(":data:releases"))
    androidTestImplementation(project(":data:settings"))
    androidTestImplementation(libs.androidx.room.runtime)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.kaspresso)
    androidTestImplementation(libs.kaspresso.compose)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android) {
        exclude(group = "org.junit.jupiter")
    }
    androidTestImplementation(libs.androidx.navigation.testing)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.register<PublishApkTask>("publishApkToLocalMaven") {
    group = "publishing"
    description = "Publishes the release APK to the local Maven repository"
    dependsOn("assembleRelease")
    releaseApkDir = layout.buildDirectory.dir("outputs/apk/release")
    mavenGroup.set(appPublish.group)
    artifactId.set(appPublish.artifactId)
    version.set(appPublish.version)
    repositoryDir.set(appPublish.repositoryDir)
}
