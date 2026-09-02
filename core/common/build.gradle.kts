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
    // SafeCallTest exercises safeCall's suspend behavior via runTest.
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    // ErrorMapperTest builds a real okhttp3.ResponseBody/MediaType to construct a retrofit2.HttpException --
    // retrofit depends on okhttp transitively, but a test that imports okhttp3 classes directly should
    // declare that dependency itself rather than rely on another library leaking it through.
    testImplementation(libs.okhttp)
}
