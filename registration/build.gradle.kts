@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.devstudioworks.registration"
    compileSdk = 33

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
}

dependencies {

    api(libs.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.credentials.play.services.auth)
    api(libs.credentials)
    api(libs.identity.googleid)
    api(libs.play.services.auth)

    api(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    api(libs.hilt.navigation.compose)
    api(libs.bundles.compose)
    api(libs.viewmodel)
    api(libs.material)

    api(project(":core-data"))


}