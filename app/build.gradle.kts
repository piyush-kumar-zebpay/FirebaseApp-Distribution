plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.firebase.appdistribution")
    alias(libs.plugins.google.gms.google.services)
}

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
        versionCode = 2
        versionName = "2.5.2"

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
    buildTypes {
        release {
            firebaseAppDistribution {
                artifactType = "APK"
                
                // Release notes - can be overridden via releaseNotesFile in CI
                releaseNotesFile = if (file("${rootProject.projectDir}/release-notes.txt").exists()) {
                    "${rootProject.projectDir}/release-notes.txt"
                } else {
                    null
                }
                
                // Default release notes when no file exists
                if (releaseNotesFile == null) {
                    releaseNotes = """
                        📱 Version: ${defaultConfig.versionName}
                        🔢 Version Code: ${defaultConfig.versionCode}
                        🌿 Branch: ${getGitBranch()}
                        👥 QA Team: piyush.k@zebpay.com
                        📜 Script: Local build via Gradle
                    """.trimIndent()
                }
                
                // Testers - can be overridden via command line: -PappDistributionTesters="email1,email2"
                val testersFromProperty = project.findProperty("appDistributionTesters") as String?
                testers = testersFromProperty ?: "piyush.k@zebpay.com"
                
                // Groups - can be set via command line: -PappDistributionGroups="qa-team,beta"
                val groupsFromProperty = project.findProperty("appDistributionGroups") as String?
                if (groupsFromProperty != null) {
                    groups = groupsFromProperty
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