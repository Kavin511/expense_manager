plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
	id("org.jetbrains.kotlin.kapt")
}

android {
	namespace = "com.devstudio.core.database"
	compileSdk = 33
	buildToolsVersion = "33.0.1"

	defaultConfig {
		minSdk = 21
		targetSdk = 33
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
	kapt(libs.androidx.room.compiler)
	implementation(libs.hilt.android)
	kapt(libs.hilt.android.compiler)
}