plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
}

android {
    lint {
        baseline = file("lint-baseline.xml")
    }
	namespace ="com.devstudio.core.data"
    compileSdk = 35
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":database"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    implementation(project(":utils"))
    implementation(project(":core-model"))
    implementation(project(":core-data:model"))
    api(libs.androidx.datastore.preferences)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.work)
    implementation(libs.firebase.crashlytics)
    implementation(libs.kotlinx.coroutines.android)
}