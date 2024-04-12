plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.0"
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.material)
                implementation(compose.components.resources)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
                implementation("com.materialkolor:material-kolor:1.4.4")
                implementation("com.google.accompanist:accompanist-flowlayout:0.19.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                implementation("media.kamel:kamel-image:0.9.1")
                implementation("com.darkrockstudios:mpfilepicker:3.1.0")
                implementation("com.eygraber:compose-color-picker:0.0.17")

                val firebaseVersion = "1.12.0"

                // firebase
                implementation("dev.gitlive:firebase-auth:$firebaseVersion")
                implementation("dev.gitlive:firebase-firestore:$firebaseVersion")
                implementation("dev.gitlive:firebase-storage:$firebaseVersion")
                implementation("dev.gitlive:firebase-common:$firebaseVersion")

                val voyagerVersion = "1.1.0-alpha03"
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")

                implementation("tech.annexflow.compose:constraintlayout-compose-multiplatform:0.3.1")

                // lottie
                implementation("io.github.ismai117:kottie:1.3.1")

                api("io.github.mirzemehdi:kmpnotifier:0.5.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation("com.google.accompanist:accompanist-permissions:0.32.0")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
dependencies {
    implementation("androidx.core:core-ktx:+")
}
