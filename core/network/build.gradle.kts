import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.example.network"
    compileSdk {
        version = release(36)
    }

    buildFeatures.buildConfig = true

    defaultConfig {
        minSdk = 24

        buildConfigField("String", "AUTH_TOKEN", getPropertyValue("network.properties", "auth.token"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(libs.bundles.common.network.api)
    implementation(libs.bundles.common.network.implementation)
    kapt(libs.bundles.common.network.kapt)
}

fun getPropertyValue(
    filename: String,
    propName: String,
): String {
    val propsFile = project.file(filename)
    check(propsFile.exists())

    val props = Properties()
    props.load(FileInputStream(propsFile))
    check(props[propName] != null)

    val rawValue = props.getProperty(propName)

    // Resolve from local.properties if value is a placeholder
    val localProps = Properties()
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localProps.load(FileInputStream(localPropsFile))
        val resolved = localProps.getProperty(rawValue)
        if (resolved != null && resolved.isNotEmpty()) {
            return "\"$resolved\""
        }
    }

    return "\"$rawValue\""
}
