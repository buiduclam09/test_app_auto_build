import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-android")
}

buildscript {
    apply(from = "../buildSrc/ktlint.gradle.kts")
}

android {
    compileSdkVersion(Versions.compile_sdk_version)
    buildToolsVersion(Versions.build_tools_version)

    flavorDimensions("default")

    defaultConfig {
        applicationId = "com.whereisit"
        minSdkVersion(Versions.min_sdk_version)
        targetSdkVersion(Versions.target_sdk_version)
        versionCode = 1
        versionName = "1_0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty(
            "archivesBaseName",
            "App-${SimpleDateFormat("HH_mm_dd_MM_yyyy").format(Calendar.getInstance().time)}-$versionName"
        )
    }

    productFlavors {
        create("DEV") {
            applicationIdSuffix = ".dev"
            versionCode = 1
            versionName = "1.0.0"

            buildConfigField("String", "END_POINT", "\"https://pokeapi.co/api/v2/\"")
        }

        create("PROD") {
            versionCode = 1
            versionName = "1.0.0"

            buildConfigField("String", "END_POINT", "\"https://pokeapi.co/api/v2/\"")
        }
    }

    signingConfigs {
        create("release") {
            val keystoreProperties = Properties().apply {
                load(
                    rootProject.file("keystore/key.properties")
                        .apply { check(exists()) }
                        .reader()
                )
            }

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile =
                rootProject.file(keystoreProperties["storeFile"] as String)
                    .apply { check(exists()) }
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro")
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = true
        }
    }

    dexOptions {
        javaMaxHeapSize = "4g"
        preDexLibraries = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        viewBinding = true
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.20")
    // App compat & design
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    //support library
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")
    // Leak canary
    // debugImplementation("com.squareup.leakcanary:leakcanary-android:2.0")
    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")
    // KTX
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")
    implementation("com.github.ThuanPx:KtExt:1.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.3")
    implementation("androidx.activity:activity-ktx:1.3.0-alpha03")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.31-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.31-alpha")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha03")
    //add firebase
    // add firebase
    implementation(platform("com.google.firebase:firebase-bom:28.3.1"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Other
    implementation("com.airbnb.android:lottie:3.3.1")
    implementation("com.jaredrummler:material-spinner:1.3.1")
    implementation("ua.zabelnikiov:swipeLayout:1.0")
    implementation("com.google.android.gms:play-services-ads:19.7.0")
    implementation(kotlin("reflect"))
}
apply {
    plugin("com.google.gms.google-services")
    plugin("com.google.firebase.crashlytics")
}
