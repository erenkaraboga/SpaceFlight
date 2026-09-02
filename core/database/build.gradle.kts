plugins {
    id("spaceflight.android.library")
    id("spaceflight.android.hilt")
}

android {
    namespace = "com.spaceflight.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    api(libs.androidx.paging.common)
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
}
