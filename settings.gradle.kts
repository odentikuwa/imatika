pluginManagement {
    plugins {
        kotlin("android") version "1.5.21"
        kotlin("androidx.navigation") version "2.4.0"
    }
    repositories {
        google()
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

rootProject.name = "imatika"
include(":app")
