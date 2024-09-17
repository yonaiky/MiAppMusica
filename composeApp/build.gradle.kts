import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
    //alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrainsCompose)
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }

    jvm("desktop")

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }

        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.desktop.currentOs)
        }

        androidMain.dependencies { }
        commonMain.dependencies {
            //implementation(compose.runtime)
            //implementation(compose.foundation)
            //implementation(compose.material3)
            //implementation(compose.ui)
            implementation(compose.components.resources)
            //implementation(compose.components.uiToolingPreview)
        }
    }
}

android {
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileSdk = 35

    defaultConfig {
        applicationId = "it.fast4x.rimusic"
        minSdk = 21
        targetSdk = 35
        versionCode = 56
        versionName = "0.6.52"
    }

    splits {
        abi {
            reset()
            isUniversalApk = true
        }
    }

    namespace = "it.fast4x.rimusic"

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "RiMusic-Debug"
        }

        release {
            vcsInfo.include = false
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders["appName"] = "RiMusic"
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                //val outputFileName = "app-${variant.baseName}-${variant.versionName}-${variant.versionCode}.apk"
                val outputFileName = "app-${variant.baseName}.apk"
                output.outputFileName = outputFileName
            }
    }

    flavorDimensions += "version"
    productFlavors {
        create("foss") {
            dimension = "version"
        }
    }
    productFlavors {
        create("accrescent") {
            dimension = "version"
            manifestPlaceholders["appName"] = "RiMusic-Acc"
        }
    }

    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
    }



    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    androidResources {
        generateLocaleConfig = true
    }

    ksp {
        arg("room.schemaLocation", "${rootProject.projectDir}/DBschemas")
    }

}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "RiMusic.DesktopApp"
            packageVersion = "1.0.0"

            /*
            val iconsRoot = project.file("desktop-icons")
            windows {
                iconFile.set(iconsRoot.resolve("icon-windows.ico"))
            }
            macOS {
                iconFile.set(iconsRoot.resolve("icon-mac.icns"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon-linux.png"))
            }

             */
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}


dependencies {
    implementation(projects.composePersist)
    implementation(projects.composeRouting)
    implementation(projects.composeReordering)
    implementation(libs.compose.activity)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.ripple)
    implementation(libs.compose.shimmer)
    implementation(libs.compose.coil)
    implementation(libs.palette)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.datasource.okhttp)
    implementation(libs.appcompat)
    implementation(libs.appcompat.resources)
    implementation(libs.core.splashscreen)
    implementation(libs.media)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.compose.ui.graphics.android)
    implementation(libs.constraintlayout)
    implementation(libs.runtime.livedata)
    implementation(libs.core.ktx)
    implementation(libs.compose.animation)
    implementation(libs.translator)
    implementation(libs.kotlin.csv)
    implementation(libs.monetcompat)
    implementation(libs.androidmaterial)
    implementation(libs.navigation)
    implementation(libs.timber)
    implementation(libs.crypto)
    implementation(libs.logging.interceptor)
    implementation(libs.math3)
    implementation(libs.toasty)
    implementation(libs.haze)
    implementation(libs.androidyoutubeplayer)
    implementation(libs.glance.widgets)
    implementation(libs.kizzy.rpc)
    implementation(libs.gson)

    implementation(libs.room)
    ksp(libs.room.compiler)

    implementation(projects.innertube)
    implementation(projects.innertubes)
    implementation(projects.kugou)
    implementation(projects.lrclib)
    implementation(projects.piped)


    coreLibraryDesugaring(libs.desugaring)
}


