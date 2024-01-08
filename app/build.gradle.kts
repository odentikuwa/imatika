val version = "2.3.7"
val kotlinVersion = "1.9.22"
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    kotlin("plugin.serialization") version "1.8.10"
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()  // 追加
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
    }
    dependencies {
        // Android Gradle Plugin アップデート
        classpath ("com.android.tools.build:gradle:8.2.0")
    }
}

android {
    namespace = "com.example.imatika"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.imatika"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {

        kotlinCompilerExtensionVersion = "1.4.3"
    }

    buildFeatures {
        compose = true
        viewBinding = true // これで viewBinding が有効になります
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime:1.5.4")
    //コンポーズのサポート
    implementation("androidx.navigation:navigation-compose:2.7.6")
    // Google Play Services Location APIを追加
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.maps.android:maps-ktx:3.2.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.android.support:support-annotations:28.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    //Activity への依存関係
    implementation("androidx.activity:activity-ktx:1.8.2")
    //kotlinx.coroutines.tasks.awaitへの依存関係
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    //coroutineScopeの依存関係
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    //httpリクエストの依存関係
    implementation("androidx.startup:startup-runtime:1.1.1")
    implementation("io.ktor:ktor-client-core:$version")
    implementation("io.ktor:ktor-client-cio:$version")
    implementation("io.ktor:ktor-client-android:$version")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    // Kotlinプラグイン
//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    // SLF4J
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.6")
}