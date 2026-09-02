plugins {
    id("spaceflight.android.library")
}

android {
    namespace = "com.spaceflight.core.common"
}

dependencies {
    api(projects.core.model)
    api(projects.designsystem)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
