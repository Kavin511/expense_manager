plugins {
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
	id("dagger.hilt.android.plugin")
	alias(libs.plugins.compose.compiler)
}

android {
	namespace = "com.devstudio.account"
	compileSdk= 34

	defaultConfig {
		minSdk = 21
		targetSdk= 34

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	buildFeatures {
		viewBinding = true
		compose = true
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
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
	implementation(libs.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.bundles.compose)
	implementation(project(":core-designSystem"))
	api(project(":core-data"))
	implementation(project(":core-model"))
	implementation(project(":core-database"))
	implementation(project(":utils"))
	implementation(libs.play.services.auth)
	implementation(libs.hilt.android)
	implementation(project(":core-model"))
	implementation(project(":core-data:model"))
	ksp(libs.hilt.android.compiler)
	implementation(libs.hilt.navigation.compose)
}

