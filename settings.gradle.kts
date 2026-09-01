pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SpaceFlight"
include(":app")
include(":designsystem")
include(":core:model")
include(":core:domain")
include(":core:database")
include(":core:network")
include(":core:common")
include(":core:data")
include(":feature:news")
include(":feature:newsdetail")
include(":feature:favorites")
