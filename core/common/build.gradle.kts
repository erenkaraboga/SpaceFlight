plugins {
    id("spaceflight.android.library")
}

android {
    namespace = "com.spaceflight.core.common"
}

dependencies {
    api(projects.core.model)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.browser)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp)
}
