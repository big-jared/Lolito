plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.google.gms.google-services")
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(platform("com.google.firebase:firebase-bom:30.0.1"))
                implementation(project(":shared"))
            }
        }
    }
}

dependencies {
    implementation("com.google.firebase:firebase-common-ktx:20.4.2")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.jaredg.micro"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.jaredg.micro"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
