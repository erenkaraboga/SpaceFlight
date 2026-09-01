plugins {
    id("spaceflight.jvm.library")
}

dependencies {
    // Article/AppError appear in these repository interfaces' own signatures, so this is `api`,
    // not `implementation` -- anyone depending on core:domain gets core:model for free.
    api(projects.core.model)
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
}
