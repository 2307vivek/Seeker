plugins {
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("org.jetbrains.compose") version "1.6.10" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.diffplug.spotless") version "5.7.0"
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            // target = "**/*.kt"
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")

            ktlint("0.40.0")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
    }
}

apply(from = "$rootDir/scripts/publish-root.gradle")