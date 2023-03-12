import Versions.arrow_version
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-android")
    id("kotlin-parcelize")
    id(Plugins.google_services)
    id(Plugins.crashlytics)
}

buildscript {
    apply(from = "../buildSrc/ktlint.gradle.kts")
}

android {
    compileSdkVersion(Versions.compile_sdk_version)
    buildToolsVersion(Versions.build_tools_version)

    flavorDimensions("default")

    defaultConfig {
        applicationId = "com.pixelteam.whereisit"
        minSdkVersion(Versions.min_sdk_version)
        targetSdkVersion(Versions.target_sdk_version)
        versionCode = 10
        versionName = "1.0.3"
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
            buildConfigField(
                "String",
                "INTERSTITIAL_AD_UNIT_ID",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField(
                "String",
                "BANNER_AD_UNIT_ID",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
        }

        create("PROD") {
            versionCode = 10
            versionName = "1.0.3"

            buildConfigField("String", "END_POINT", "\"https://pokeapi.co/api/v2/\"")
            buildConfigField(
                "String",
                "INTERSTITIAL_AD_UNIT_ID",
                "\"ca-app-pub-7926716557588074/4967328324\""
            )
            buildConfigField(
                "String",
                "BANNER_AD_UNIT_ID",
                "\"ca-app-pub-7926716557588074/3574495220\""
            )
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
            isDebuggable = false
        }
    }

    dexOptions {
        javaMaxHeapSize = "4g"
        preDexLibraries = true
    }


    buildFeatures {
        viewBinding = true
    }

    kapt {
        useBuildCache = true
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
    kapt {
        javacOptions {
            option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
    // App compat & design
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //support library
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
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
    implementation("androidx.work:work-runtime:2.7.1")
    // KTX
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("com.github.ThuanPx:KtExt:1.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    implementation("androidx.activity:activity-ktx:1.6.0-alpha05")
    //add firebase
    implementation(platform("com.google.firebase:firebase-bom:30.0.1"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Other
    implementation("com.airbnb.android:lottie:3.3.1")
    implementation("com.jaredrummler:material-spinner:1.3.1")
    implementation("ua.zabelnikiov:swipeLayout:1.0")
    implementation("com.google.android.gms:play-services-ads:21.1.0")
    implementation("com.google.android.gms:play-services-basement:18.1.0")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.43")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.github.hoc081098:FlowExt-jvm:0.4.0")
    kapt("com.google.dagger:hilt-android-compiler:2.43")
    //arrow
    implementation("io.arrow-kt:arrow-core:$arrow_version")
}
apply {
    plugin("com.google.gms.google-services")
    plugin("com.google.firebase.crashlytics")
}