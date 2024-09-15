import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.library)
}

kotlin {
//@OptIn(ExperimentalWasmDsl::class)
/*
    wasmJs {
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
    }
 */

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    /*
    iosX64()
    iosArm64()
    iosSimulatorArm64()
     */

    jvm()

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            //implementation(projects.innertube)
            //implementation(projects.lrclib)
            //implementation(projects.kugou)
            //implementation(projects.piped)
        }
        androidMain.dependencies {
            // put your Multiplatform dependencies here
            //implementation(projects.androidApp)
        }
    }
}


android {
    namespace = "it.fast4x.rimusic.shared"
    compileSdk = 35 //libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = 21 //libs.versions.android.minSdk.get().toInt()
    }
}

