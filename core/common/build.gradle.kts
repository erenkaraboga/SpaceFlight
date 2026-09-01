import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.spaceflight.core.common"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // AppError/UiText appear in this module's own public functions (toUiText(), safeCall()'s
    // Result<T> failure channel), so both are `api`.
    api(projects.core.model)
    api(projects.designsystem)

    implementation(libs.kotlinx.coroutines.core)
    // ErrorMapper only needs these for the exception TYPES it pattern-matches on
    // (retrofit2.HttpException, kotlinx.serialization.SerializationException) -- it never makes
    // a network call itself.
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
}
