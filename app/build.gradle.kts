plugins {
    id("com.android.application")
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

android {
    namespace = "dev.vivvvek.seekerdemo"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "dev.vivvvek.seekerdemo"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    androidTarget()

    sourceSets {
        androidMain{
            dependsOn(commonMain.get())
            dependencies {
                implementation("androidx.activity:activity-compose:1.7.2")
                implementation("androidx.compose.material:material:1.5.1")
            }
        }
        val commonMain by getting
        commonMain.dependencies {
            implementation(project(":seeker"))

            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.preview)

            implementation(compose.uiTooling)
            implementation(compose.uiUtil)

            val mokoMvvm = "0.16.1"
            api("dev.icerock.moko:mvvm-core:$mokoMvvm")
            api("dev.icerock.moko:mvvm-compose:$mokoMvvm")
            api("dev.icerock.moko:mvvm-flow-compose:$mokoMvvm")
        }
    }
}

val compose_ui_version: String by extra

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
}