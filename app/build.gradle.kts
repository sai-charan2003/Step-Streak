import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.mikepenz.aboutlibrary)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.charan.stepstreak"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.charan.stepstreak"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"

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
        compose = true
        buildConfig = true
    }
    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                load(project.rootProject.file("local.properties").inputStream())
            }
            keyAlias = properties.getProperty("KEY_ALIAS") ?: ""
            keyPassword = properties.getProperty("KEY_PASSWORD") ?: ""
            storeFile = file(properties.getProperty("KEY_LOCATION") ?: "")
            storePassword = properties.getProperty("KEY_STORE_PASSWORD") ?: ""
        }
    }
    hilt {
        enableAggregatingTask = false
    }
    applicationVariants.all {
        val variant = this
        variant.outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "Step-Streak-${variant.buildType.name}-${variant.versionName}.apk"
                output.outputFileName = outputFileName
            }
    }


    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            resValue("string","app_name","Step Streak-Debug")


        }
        release {
            isMinifyEnabled = false
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation (libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.connect.client)
    implementation (libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance)
    implementation(libs.glance.material)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.converter.gson)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.hilt.android)
    ksp (libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)
    implementation (libs.accompanist.permissions)
    implementation (libs.androidx.hilt.work)
    implementation(libs.splash.screen)
    implementation(libs.kotlinx.serialization.json)
    implementation (libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.m3)
    ksp(libs.androidx.room.compiler)
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    implementation (libs.accompanist.drawablepainter)
    implementation (libs.androidx.datastore)
    implementation(libs.androidx.work.runtime)
    implementation( libs.lottie)
    implementation(libs.vico.compose.m3)
    implementation(libs.charts)
    implementation(libs.charty)
    implementation (libs.androidx.graphics.shapes)
}