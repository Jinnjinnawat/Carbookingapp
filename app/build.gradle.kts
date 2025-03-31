plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.carbookingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.carbookingapp"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true // เปิดใช้งาน View Binding
    }
}

dependencies {
    // AndroidX Components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5") // หรือเวอร์ชันล่าสุด
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.android.volley:volley:1.2.1")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.firestore)
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    // Third-party libraries
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation ("com.google.firebase:firebase-auth:22.1.2")
    implementation ("androidx.cardview:cardview:1.0.0")
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}