plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.erickvazquezs.mantenteencontacto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.erickvazquezs.mantenteencontacto"
        minSdk = 28
        targetSdk = 36
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.tbuonomo:dotsindicator:5.1.0") {
        exclude(group = "androidx.fragment", module = "fragment")
        exclude(group = "androidx.lifecycle")
        exclude(group = "androidx.viewpager2", module = "viewpager2")
    }

    implementation ("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation ("androidx.navigation:navigation-ui-ktx:2.9.5")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    // Analytics
    implementation("com.google.firebase:firebase-analytics")
    // Auth
    implementation("com.google.firebase:firebase-auth")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}