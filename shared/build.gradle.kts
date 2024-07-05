plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinNativeCocoapods)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.serialization)

}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        ios.deploymentTarget = "14.1"
        version = "1.0.0"
        summary = "Shared module of Micro"
        homepage = "https://github.com/big-jared/Lolito"
        framework {
            baseName = "shared"
            isStatic = true
            export(libs.kmpNotifier)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(libs.bundles.koin)
                implementation(libs.bundles.voyager)
                implementation(libs.bundles.firebase.kt)
                implementation(libs.bundles.ktor)
                implementation(libs.multiplatformSettings.noargs)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.napier)
                api(libs.kmpNotifier)
                implementation(libs.kmpAuth.firebase)
                implementation(libs.kmpAuth.uihelper)
                implementation(libs.kmpAuth.google)
                implementation(libs.lottie)

                implementation(libs.coil.compose)
                implementation(libs.coil.ktor)

                implementation(libs.kmprevenuecat.purchases)
                implementation(libs.kmprevenuecat.purchases.ui)

                implementation(libs.file.picker)
                implementation(libs.color.picker)
                implementation(libs.material.kolor)
                implementation(libs.flow.layout)
                implementation(libs.kamel.image)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.activity.compose)
                api(libs.androidx.appcompat)
                api(libs.androidx.core)
                api(libs.androidx.lifecycle.runtime.compose)
                api(libs.koin.android)

                //Firebase
                api(project.dependencies.platform(libs.firebase.bom))
                api(libs.firebase.analytics)
                api(libs.firebase.crashlytics)
                api(libs.firebase.messaging)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
    task("testClasses")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.jaredg.micro"

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/commonMain/composeResources")
        }
    }

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