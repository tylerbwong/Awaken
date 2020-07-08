plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "io.awaken"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        isAbortOnError = false
    }
}

dependencies {
    testImplementation("junit:junit:4.13")

    // android
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.room:room-runtime:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.1.0")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")

    // network
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.6")
}
