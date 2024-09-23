import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled


plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrainsCompose)
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://dl.bintray.com/caprica/vlcj/")
}

kotlin {
    jvmToolchain(17)
    jvm()
    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }

        }

        java.sourceSets {
            all {
                java.srcDir("src/main/kotlin")
            }
        }

        jvmMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.6.11")
            implementation("uk.co.caprica:vlcj:4.8.2")
        }
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}

javafx {
    version = "19"
    modules("javafx.media", "javafx.swing")
}