plugins {
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
}

android {
	namespace = "com.devstudio.core.database"
	compileSdk= 34
	buildToolsVersion = "33.0.1"

	defaultConfig {
		minSdk = 21
		targetSdk= 34
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
	}

	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
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
	implementation(libs.androidx.room)
	ksp(libs.androidx.room.compiler)
	implementation(libs.hilt.android)
	ksp(libs.hilt.android.compiler)
}