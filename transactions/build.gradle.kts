plugins {
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
	id("dagger.hilt.android.plugin")
	alias(libs.plugins.compose.compiler)
}

android {
	namespace = "com.devstudio.transactions"
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

	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.6"
	}

	lint {
		abortOnError = false
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
	implementation(project(":utils"))
	implementation(project(":core-designSystem"))
	implementation(project(":core-database"))
	api(project(":core-data"))
	implementation(project(":core-data:model"))
	implementation(project(":core-model"))
	implementation(libs.hilt.android)
	implementation(libs.androidx.room)
	ksp(libs.hilt.android.compiler)
	implementation(libs.hilt.navigation.compose)
	implementation(libs.bundles.compose)

	implementation(libs.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	androidTestImplementation(libs.androidx.test.junit)
	testImplementation(libs.junit)
}

