import com.google.firebase.appdistribution.gradle.firebaseAppDistribution

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.firebase.appdistribution")
    alias(libs.plugins.google.gms.google.services)
}

// Get config values from command line or use defaults
val appDistTesters: String = (project.findProperty("appDistributionTesters") as? String)
    ?: "piyush.k@zebpay.com"

val appDistGroups: String = (project.findProperty("appDistributionGroups") as? String)
    ?: "QA"

val appDistReleaseNotes: String? = project.findProperty("appDistributionReleaseNotes") as? String

android {
    namespace = "com.example.firebaseappdistribution"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.firebaseappdistribution"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            firebaseAppDistribution {
                artifactType = "APK"
                testers = appDistTesters
                groups = appDistGroups
                
                // Release notes: command line > file > auto-generated
                val notesFile = rootProject.file("release-notes.txt")
                if (appDistReleaseNotes != null) {
                    releaseNotes = appDistReleaseNotes
                } else if (notesFile.exists()) {
                    releaseNotesFile = notesFile.absolutePath
                } else {
                    releaseNotes = "Version: ${android.defaultConfig.versionName} (${android.defaultConfig.versionCode})"
                }
            }
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    
    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}