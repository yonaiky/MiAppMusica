plugins {
    kotlin("jvm")
}

sourceSets.all {
    java.srcDir("src/$name/kotlin")
}

dependencies {
    implementation(libs.ktor.encoding)
    implementation(libs.brotli)
}