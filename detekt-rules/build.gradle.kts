plugins {
    kotlin("jvm")
    alias(libs.plugins.detekt)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.detekt.api)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.detekt.test)
    testImplementation(libs.detekt.test.utils)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit)
}
