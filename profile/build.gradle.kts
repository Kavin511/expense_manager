plugins {
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.android)
	id("org.jetbrains.kotlin.kapt")
	id("dagger.hilt.android.plugin")
	alias(libs.plugins.compose.compiler)
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
	namespace = "com.devstudio.account"
	compileSdk = 33

	defaultConfig {
		minSdk = 21
		targetSdk = 33

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	buildFeatures {
		viewBinding = true
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.6"
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
	implementation(project(":core-data"))
	implementation(project(":core-model"))
	implementation(project(":core-database"))
	implementation(project(":utils"))
	implementation(libs.play.services.auth)
	implementation(libs.hilt.android)
	implementation(project(":core-model"))
	implementation(project(":core-data:model"))
	kapt(libs.hilt.android.compiler)
	implementation(libs.hilt.navigation.compose)
	implementation(libs.androidx.dataStore.core)
	implementation(libs.protobuf.kotlin.lite)
}

