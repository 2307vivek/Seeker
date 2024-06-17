import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.android.application")
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}


// https://github.com/JetBrains/compose-multiplatform/issues/3123#issuecomment-1647435023
val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val targetArch = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val version = "0.8.8" // or any more recent version
val target = "${targetOs}-${targetArch}"

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

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "app"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting
        commonMain.dependencies {
            implementation(project(":seeker"))

            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.runtime)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
            implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0")
        }
        androidMain {
            dependsOn(commonMain)
            dependencies {
                implementation("androidx.activity:activity-compose:1.7.2")
                implementation("androidx.compose.material:material:1.5.1")
                implementation(compose.preview)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        iosMain.dependsOn(commonMain)

        val jvmMain by getting
        jvmMain.dependencies {
            implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$version")
            implementation("androidx.collection:collection:1.4.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
            implementation(compose.desktop.currentOs)
        }
        jvmMain.dependsOn(commonMain)
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

compose.desktop {
    application {
        mainClass = "dev.vivvvek.seekerdemo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.vivvvek.seekerdemo"
            packageVersion = "1.0.0"
        }
    }
}
