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
    // PagingSource appears in ArticleDao's own public API.
    api(libs.androidx.paging.common)

    // SpaceflightDatabase's own supertype (RoomDatabase) and the withTransaction extension are
    // used directly by core:data (ArticleRemoteMediator), so both need to be `api`, not
    // `implementation` -- otherwise consumers can declare a Room database but can't touch it.
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
}
