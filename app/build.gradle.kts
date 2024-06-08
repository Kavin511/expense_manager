plugins {
	alias(libs.plugins.androidApplication)
	id("org.jetbrains.kotlin.android")
	id("org.jetbrains.kotlin.kapt")
	id("com.google.gms.google-services")
	id("com.google.firebase.crashlytics")
	id("dagger.hilt.android.plugin")
	alias(libs.plugins.protobuf)
}

protobuf {
	protoc {
		artifact = libs.protobuf.protoc.get().toString()
	}
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				register("java") {
					option("lite")
				}
				register("kotlin") {
					option("lite")
				}
			}
		}
	}
}

android {
	namespace = "com.devstudio.expensemanager"
	compileSdk = 34
	buildToolsVersion = "33.0.1"
	signingConfigs {
		create("release") {
			storeFile = file("../devstudiowork.jks")
			storePassword = "devstudioworks"
			keyAlias = "key0"
			keyPassword = "devstudioworks"
		}
	}

	defaultConfig {
		applicationId = "com.devstudio.expensemanager"
		minSdk = 21
		targetSdk = 34
		versionCode = 24
		versionName = "1.1.8"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			signingConfig = signingConfigs.getByName("release")
		}
		getByName("debug") {
			applicationIdSuffix = ".debug"
		}
	}
	buildFeatures {
		viewBinding = true
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.6"
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	namespace = "com.devstudio.expensemanager"
}

dependencies {
	implementation(project(":utils"))
	implementation(project(":core-designSystem"))
	implementation(project(":core-database"))
	implementation(project(":core-data"))
	implementation(project(":core-model"))
	implementation(project(":transactions"))
	implementation(project(":category"))
	implementation(project(":profile"))
	implementation(project(":books"))

	implementation(libs.androidx.core)
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.compose.material3)
	implementation(libs.constraintlayout)
	implementation(libs.livedata)
	implementation(libs.viewmodel)
	implementation(libs.firebase.crashlytics)
	implementation(libs.firebase.analytics)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.test.junit)
	androidTestImplementation(libs.androidx.test.espresso)
	testImplementation(libs.google.truth)
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	androidTestImplementation(libs.androidx.room.room.testing)

	implementation(libs.bundles.compose)

	//    room
	implementation(libs.androidx.room)
	kapt(libs.androidx.room.compiler)

	implementation(libs.hilt.android)
	implementation(libs.hilt.navigation.compose)
	kapt(libs.hilt.android.compiler)
	implementation(libs.androidx.work)
	kapt(libs.androidx.hilt.compiler)
	implementation(project(":core-data:model"))

	implementation(libs.androidx.dataStore.core)
	implementation(libs.protobuf.kotlin.lite)
}