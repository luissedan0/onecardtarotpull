import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    // Suppress Beta warning for expect/actual classes (used by Room KMP's @ConstructedBy pattern)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            // Koin — Android platform module
            implementation(libs.koin.android)
            // Ktor — Android HTTP engine
            implementation(libs.ktor.client.android)
            // Coil — network image loader (Ktor-backed)
            implementation(libs.coil3.network.ktor)
        }

        commonMain.dependencies {
            // Compose UI + Material Icons (core set covers all icons we use)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.material.icons.extended)

            // Lifecycle & ViewModel
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation (KMP)
            implementation(libs.navigation.compose)

            // Koin — DI core + Compose integration + Compose ViewModel
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Room (KMP) + SQLite bundled driver (required for iOS)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // DataStore Preferences (KMP)
            implementation(libs.datastore.preferences)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)

            // Kotlinx DateTime
            implementation(libs.kotlinx.datetime)

            // Ktor — HTTP client core + JSON content negotiation
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)

            // Coil — image loading (Compose integration)
            implementation(libs.coil3.compose)
        }

        iosMain.dependencies {
            // Ktor — iOS/Darwin HTTP engine
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.espresso.core)
        }
    }
}

// Room schema output directory
room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.luissedan0.onecardtarotpull"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.luissedan0.onecardtarotpull"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)

    // Room KSP annotation processor — must be declared per target
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}
