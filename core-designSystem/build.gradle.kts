import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

kotlin {
    androidTarget()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "theme"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.components.uiToolingPreview)
            api(compose.components.resources)
            implementation(libs.navigation.compose)
            api(compose.components.uiToolingPreview)
            implementation(libs.material.icons.extended)
            implementation(project(":theme"))
        }

        commonTest.dependencies {
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.activity.compose)
            implementation(libs.core.ktx)
            implementation(libs.material)
            implementation(libs.androidx.appcompat)
            implementation(libs.bundles.compose)
        }

        iosMain.dependencies {
        }

    }

}

android {
    lint {
        baseline = file("lint-baseline.xml")
    }
    namespace = "com.devstudio.designSystem"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.androidx.ui.tooling.preview.android)
}
