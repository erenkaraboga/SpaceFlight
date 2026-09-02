plugins {
    id("spaceflight.jvm.library")
}

dependencies {
    api(projects.core.model)
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
    implementation(libs.javax.inject)
    testImplementation(libs.junit)
}
