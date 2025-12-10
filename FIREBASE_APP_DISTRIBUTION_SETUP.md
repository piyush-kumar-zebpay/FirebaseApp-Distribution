# Firebase App Distribution Setup Guide

This document provides a complete guide to setting up Firebase App Distribution for your Android project.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Firebase Console Setup](#firebase-console-setup)
3. [Gradle Configuration](#gradle-configuration)
4. [Authentication Setup](#authentication-setup)
5. [Build and Upload Commands](#build-and-upload-commands)
6. [Configuration Details](#configuration-details)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before setting up Firebase App Distribution, ensure you have:

- [ ] A Firebase project created in the [Firebase Console](https://console.firebase.google.com/)
- [ ] An Android app registered in your Firebase project
- [ ] Firebase CLI installed (`npm install -g firebase-tools`)
- [ ] Android Studio with Gradle 8.x or higher
- [ ] JDK 11 or higher

---

## Firebase Console Setup

### Step 1: Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Enter your project name
4. Follow the setup wizard to complete project creation

### Step 2: Register Your Android App

1. In your Firebase project, click the Android icon to add an Android app
2. Enter your app's package name: `com.example.firebaseappdistribution`
3. (Optional) Enter app nickname and debug signing certificate SHA-1
4. Click **"Register app"**

### Step 3: Download Configuration File

1. Download the `google-services.json` file
2. Place it in your app module directory: `app/google-services.json`

### Step 4: Enable App Distribution

1. In Firebase Console, navigate to **Release & Monitor → App Distribution**
2. Click **"Get started"** if this is your first time
3. Add testers or create tester groups

---

## Gradle Configuration

### Root-level `build.gradle.kts`

```kotlin
// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.firebase.appdistribution") version "5.2.0" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
```

### App-level `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.firebase.appdistribution")
    alias(libs.plugins.google.gms.google.services)
}

// Helper function to get current Git branch
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
            
            // Firebase App Distribution configuration
            firebaseAppDistribution {
                artifactType = "APK"
                releaseNotes = """
                    📱 Version: ${defaultConfig.versionName}
                    🔢 Version Code: ${defaultConfig.versionCode}
                    🌿 Branch: ${getGitBranch()}
                    👥 QA Team: your-email@example.com
                    📜 Script: release build via Gradle
                """.trimIndent()
                testers = "your-email@example.com"
            }
        }
    }
    
    // ... rest of android configuration
}
```

### Version Catalog `gradle/libs.versions.toml`

```toml
[versions]
agp = "8.13.1"
kotlin = "2.0.21"
googleGmsGoogleServices = "4.4.4"
googleServices = "4.4.2"
firebaseAppdistribution = "5.0.0"

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
firebase-appdistribution = { id = "com.google.firebase.appdistribution", version.ref = "firebaseAppdistribution" }
google-gms-google-services = { id = "com.google.gms.google-services", version.ref = "googleGmsGoogleServices" }
```

---

## Authentication Setup

You have **two options** for authenticating with Firebase:

### Option 1: Firebase CLI Login (Recommended for Local Development)

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Verify login
firebase projects:list
```

### Option 2: Service Account (Recommended for CI/CD)

1. Go to **Firebase Console → Project Settings → Service accounts**
2. Click **"Generate new private key"**
3. Save the JSON file securely (e.g., `key.json`)
4. Add to your `app/build.gradle.kts`:

```kotlin
firebaseAppDistribution {
    artifactType = "APK"
    serviceCredentialsFile = "path/to/your/key.json"
    releaseNotes = "Your release notes"
    testers = "tester@example.com"
}
```

> ⚠️ **Security Note**: Never commit your `key.json` to version control! Add it to `.gitignore`.

---

## CI/CD with GitHub Actions

This project includes a GitHub Actions workflow that allows **any developer** to build and upload to Firebase App Distribution from **any branch**.

### Features

- ✅ **Automatic builds** on push to `main`, `develop`, and `release/**` branches
- ✅ **Manual trigger** from any branch via GitHub Actions UI
- ✅ **Custom release notes** when triggering manually
- ✅ **Dynamic testers/groups** configuration
- ✅ **Build artifacts** saved for 30 days
- ✅ **Secure authentication** via GitHub Secrets

### Setup Instructions

#### Step 1: Create a Firebase Service Account

1. Go to **Firebase Console** → **Project Settings** → **Service accounts**
2. Click **"Generate new private key"**
3. A JSON file will be downloaded - keep it secure!

#### Step 2: Add the Secret to GitHub

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **"New repository secret"**
4. Name: `FIREBASE_SERVICE_ACCOUNT`
5. Value: Paste the **entire contents** of the downloaded JSON file
6. Click **"Add secret"**

#### Step 3: Push and Test

The workflow will automatically run on pushes to configured branches, or you can trigger it manually.

### Manual Trigger

1. Go to **Actions** tab in your GitHub repository
2. Select **"Build & Upload to Firebase App Distribution"** workflow
3. Click **"Run workflow"**
4. Select your branch and fill in optional fields:
   - **Custom release notes**: Add any notes for this build
   - **Tester emails**: Override default testers (comma-separated)
   - **Tester groups**: Specify tester groups (comma-separated)
5. Click **"Run workflow"**

### Workflow Configuration

The workflow file is located at `.github/workflows/firebase-app-distribution.yml`

```yaml
# Automatic triggers
on:
  push:
    branches:
      - main
      - develop
      - release/**
  
  # Manual trigger
  workflow_dispatch:
    inputs:
      release_notes:
        description: 'Custom release notes'
      testers:
        description: 'Tester emails'
      groups:
        description: 'Tester groups'
```

### Adding More Team Members

All developers with **write access** to the repository can:
- Trigger manual builds from any branch
- View build status and logs
- Download build artifacts

No Firebase CLI login or individual Firebase project access required!

---

## Build and Upload Commands

### Build and Upload Release APK

```bash
# Windows
.\gradlew assembleRelease appDistributionUploadRelease

# macOS/Linux
./gradlew assembleRelease appDistributionUploadRelease
```

### Build Only (Without Upload)

```bash
# Windows
.\gradlew assembleRelease

# macOS/Linux
./gradlew assembleRelease
```

### Clean and Build

```bash
# Windows
.\gradlew clean assembleRelease appDistributionUploadRelease

# macOS/Linux
./gradlew clean assembleRelease appDistributionUploadRelease
```

---

## Configuration Details

### Firebase App Distribution Options

| Property | Description | Example |
|----------|-------------|---------|
| `artifactType` | Type of build artifact | `"APK"` or `"AAB"` |
| `releaseNotes` | Notes shown to testers | `"Bug fixes and improvements"` |
| `releaseNotesFile` | Path to file with notes | `"path/to/notes.txt"` |
| `testers` | Comma-separated emails | `"a@test.com, b@test.com"` |
| `testersFile` | Path to file with emails | `"path/to/testers.txt"` |
| `groups` | Tester group aliases | `"qa-team, beta-testers"` |
| `groupsFile` | Path to file with groups | `"path/to/groups.txt"` |
| `serviceCredentialsFile` | Service account JSON | `"path/to/key.json"` |

### Current Project Configuration

```kotlin
firebaseAppDistribution {
    artifactType = "APK"
    releaseNotes = """
        📱 Version: ${defaultConfig.versionName}
        🔢 Version Code: ${defaultConfig.versionCode}
        🌿 Branch: ${getGitBranch()}
        👥 QA Team: piyush.k@zebpay.com
        📜 Script: release build via Gradle
    """.trimIndent()
    testers = "piyush.k@zebpay.com"
}
```

---

## Project Structure

```
FirebaseAppDistribution/
├── app/
│   ├── build.gradle.kts          # App-level Gradle config
│   ├── google-services.json      # Firebase configuration
│   └── src/
│       └── main/
│           └── ...
├── gradle/
│   └── libs.versions.toml        # Version catalog
├── build.gradle.kts              # Root-level Gradle config
├── settings.gradle.kts           # Settings
└── gradlew / gradlew.bat         # Gradle wrapper
```

---

## Troubleshooting

### Common Issues and Solutions

#### 1. Authentication Error

**Error**: `FirebaseAppDistributionException: Authentication failed`

**Solution**: 
```bash
# Re-login to Firebase
firebase logout
firebase login
```

#### 2. Missing google-services.json

**Error**: `File google-services.json is missing`

**Solution**: Download from Firebase Console → Project Settings → Your App → Download `google-services.json`

#### 3. Plugin Not Found

**Error**: `Plugin 'com.google.firebase.appdistribution' not found`

**Solution**: Ensure the plugin is added in root `build.gradle.kts`:
```kotlin
id("com.google.firebase.appdistribution") version "5.2.0" apply false
```

#### 4. Build Variant Not Found

**Error**: `Task 'appDistributionUploadRelease' not found`

**Solution**: Make sure you're using the correct variant name. Available commands:
- `appDistributionUploadRelease` - for release build type
- `appDistributionUploadDebug` - for debug build type

#### 5. Version Conflict

**Error**: Gradle version conflicts

**Solution**: Update to compatible versions in `libs.versions.toml`:
```toml
firebaseAppdistribution = "5.0.0"
googleGmsGoogleServices = "4.4.4"
```

---

## Quick Reference

### Minimum Setup Checklist

1. ✅ Add Firebase App Distribution plugin to root `build.gradle.kts`
2. ✅ Apply plugin in `app/build.gradle.kts`
3. ✅ Configure `firebaseAppDistribution` block in release build type
4. ✅ Add `google-services.json` to `app/` directory
5. ✅ Login via Firebase CLI: `firebase login`
6. ✅ Run: `./gradlew assembleRelease appDistributionUploadRelease`

### Useful Commands

| Command | Description |
|---------|-------------|
| `firebase login` | Authenticate with Firebase |
| `firebase logout` | Log out of Firebase |
| `firebase projects:list` | List available projects |
| `./gradlew tasks` | List all available Gradle tasks |
| `./gradlew assembleRelease` | Build release APK |
| `./gradlew appDistributionUploadRelease` | Upload to App Distribution |

---

## Additional Resources

- [Firebase App Distribution Documentation](https://firebase.google.com/docs/app-distribution)
- [Gradle Plugin Reference](https://firebase.google.com/docs/app-distribution/android/distribute-gradle)
- [Firebase CLI Documentation](https://firebase.google.com/docs/cli)

---

*Document Last Updated: December 2024*
