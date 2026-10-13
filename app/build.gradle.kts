plugins {
    id("com.android.application")
}

android {
    namespace = "com.rutok.ultra"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rutok.ultra"
        minSdk = 23
        targetSdk = 28
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.viewpager2:viewpager2:1.1.0")
}
