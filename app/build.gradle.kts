plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.isarthaksharma.facefusion"
    compileSdk = 34

    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.isarthaksharma.facefusion"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "2.1.0"

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

    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.mlkit.face.detection)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.mlkit:face-detection:16.1.6")

    // Text Reorganization

    // To recognize Latin script
    implementation ("com.google.mlkit:text-recognition:16.0.0")
    // To recognize Chinese script
    implementation ("com.google.mlkit:text-recognition-chinese:16.0.0")
    // To recognize Devanagari script
    implementation ("com.google.mlkit:text-recognition-devanagari:16.0.0")
    // To recognize Japanese script
    implementation ("com.google.mlkit:text-recognition-japanese:16.0.0")
    // To recognize Korean script
    implementation ("com.google.mlkit:text-recognition-korean:16.0.0")

}