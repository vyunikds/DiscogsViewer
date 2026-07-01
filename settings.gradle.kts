pluginManagement {
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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DiscogsViewer"
include(":app")
include(":core:network")
include(":core:database")
include(":core:di")
include(":core:basepresentation")
include(":data:releases")
include(":data:search")
include(":data:favorite")
include(":data:settings")
include(":feature:releases")
include(":feature:search")
include(":feature:favorites")
include(":feature:details")
include(":feature:settings")
