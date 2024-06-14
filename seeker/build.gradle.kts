plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

extra["PUBLISH_GROUP_ID"] = "io.github.2307vivek"
extra["PUBLISH_VERSION"] = "1.2.2"
extra["PUBLISH_ARTIFACT_ID"] = "seeker"

apply("${rootProject.projectDir}/scripts/publish-module.gradle")

android {
    namespace = "dev.vivvvek.seeker"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting
        commonMain.dependencies {
            implementation(compose.material)
            implementation(compose.uiTooling)
        }
    }
}

val compose_ui_version: String by extra
dependencies {
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    androidTestImplementation("androidx.compose.ui:ui-test:$compose_ui_version")
}
