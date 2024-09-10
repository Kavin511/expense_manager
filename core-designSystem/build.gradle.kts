plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
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
    namespace = "com.devstudio.designSystem"
    compileSdk = 34
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
