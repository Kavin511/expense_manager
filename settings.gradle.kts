pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "ExpenseManager"
include(":app")
include(":core-designSystem")
include(":utils")
include(":core-data")
include(":transactions")
include(":core-model")
include(":category")
include(":profile")
include(":books")
include(":core-data:model")
include(":sharedModule")
include(":theme")
include(":database")
