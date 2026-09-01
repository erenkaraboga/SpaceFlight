plugins {
    id("spaceflight.android.library")
    id("spaceflight.android.compose")
    id("spaceflight.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.spaceflight.feature.newsdetail"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.common)
    api(projects.designsystem)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.ui.tooling.preview)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}
