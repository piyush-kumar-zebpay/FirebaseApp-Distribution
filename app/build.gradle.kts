plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.firebase.appdistribution")
    alias(libs.plugins.google.gms.google.services)
}

// Import to fix deprecation warning for firebaseAppDistribution in buildTypes
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution

fun getGitBranch(): String {
    return try {
        val process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD")
        process.inputStream.bufferedReader().readText().trim()
    } catch (e: Exception) { "unknown" }
}

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
                
                // Get values from command line properties, or use defaults
                val testersFromProperty = project.findProperty("appDistributionTesters") as String?
                val groupsFromProperty = project.findProperty("appDistributionGroups") as String?
                
                // Default testers - ALWAYS set to ensure distribution happens
                testers = testersFromProperty ?: "piyush.k@zebpay.com"
                
                // Default group - ALWAYS set to ensure distribution happens
                groups = groupsFromProperty ?: "qa"
                
                // Release notes - use file if exists (CI), otherwise default text
                if (file("${rootProject.projectDir}/release-notes.txt").exists()) {
                    releaseNotesFile = "${rootProject.projectDir}/release-notes.txt"
                } else {
                    releaseNotes = """
                        📱 Version: ${defaultConfig.versionName}
                        🔢 Version Code: ${defaultConfig.versionCode}
                        🌿 Branch: ${getGitBranch()}
                        👥 Testers: ${testers}
                        👥 Groups: ${groups}
                        📜 Script: release build via Gradle
                    """.trimIndent()
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