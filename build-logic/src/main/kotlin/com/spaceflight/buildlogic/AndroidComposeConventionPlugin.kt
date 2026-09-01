package com.spaceflight.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Applied to both `:app` and any Compose-using library module.
 *
 * Gradle's extension lookup only walks supertypes for a plain (non-generic) requested type -- a
 * generic `CommonExtension<*, *, *, *, *, *>` lookup fails at runtime ("Extension of type
 * 'CommonExtension<?, ?, ?, ?, ?, ?>' does not exist") even though both `ApplicationExtension` and
 * `LibraryExtension` extend it. So this branches on whichever concrete extension is actually
 * registered instead of asking for the shared supertype directly.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> { enableCompose() }
            }

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> { enableCompose() }
            }
        }
    }

    private fun CommonExtension<*, *, *, *, *, *>.enableCompose() {
        buildFeatures {
            compose = true
        }
    }
}
