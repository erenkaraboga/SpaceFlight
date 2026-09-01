plugins {
    id("spaceflight.android.library")
    id("spaceflight.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.spaceflight.core.network"

    defaultConfig {
        buildConfigField("String", "API_BASE_URL", "\"https://api.spaceflightnewsapi.net/v4/\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.noop)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
}
