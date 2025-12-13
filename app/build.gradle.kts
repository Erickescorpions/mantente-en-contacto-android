import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.erickvazquezs.mantenteencontacto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.erickvazquezs.mantenteencontacto"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if(localPropertiesFile.exists()){
            localProperties.load(localPropertiesFile.inputStream())
            val mapsApiKey = localProperties.getProperty("MAPS_API_KEY", "")
            buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
            manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        }

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
        buildConfig = true
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

    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    // Analytics
    implementation("com.google.firebase:firebase-analytics")
    // Auth
    implementation("com.google.firebase:firebase-auth")
    // Firestore
    implementation("com.google.firebase:firebase-firestore")
    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}