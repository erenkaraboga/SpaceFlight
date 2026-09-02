plugins {
    id("spaceflight.android.library")
    id("spaceflight.android.compose")
}

android {
    namespace = "com.spaceflight.designsystem"
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.foundation)
    api(libs.androidx.animation)
    api(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose)
    api(libs.haze)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
