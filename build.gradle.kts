buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.ksp.gradlePlugin)
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.org.jetbrains.kotlin.jvm).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.ksp).apply(false)
    id("com.google.dagger.hilt.android") version "2.42" apply false
    id("com.android.dynamic-feature") version "8.2.2" apply false
    id("com.diffplug.spotless") version "6.19.0" apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")

            ktlint()
            // licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }

        groovyGradle {
            target("*.gradle")
            greclipse()
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
// tasks.register<Build>("reBuild")