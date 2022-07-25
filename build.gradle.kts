buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath(ClassPaths.google_services)
        classpath(ClassPaths.crashlytics)
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { setUrl("https://jitpack.io") }
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.module.group == "org.jetbrains.kotlin" && requested.module.name.startsWith(
                    "kotlin-stdlib"
                )
            ) {
                useVersion("1.6.21")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
