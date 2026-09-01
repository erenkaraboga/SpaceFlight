plugins {
    `kotlin-dsl`
}

group = "com.spaceflight.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "spaceflight.android.application"
            implementationClass = "com.spaceflight.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "spaceflight.android.library"
            implementationClass = "com.spaceflight.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "spaceflight.android.compose"
            implementationClass = "com.spaceflight.buildlogic.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "spaceflight.android.hilt"
            implementationClass = "com.spaceflight.buildlogic.AndroidHiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = "spaceflight.jvm.library"
            implementationClass = "com.spaceflight.buildlogic.JvmLibraryConventionPlugin"
        }
    }
}
