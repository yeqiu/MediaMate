plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    // 发布到 Maven
    id("maven-publish")
}

android {
    namespace = "com.yeqiu.mediamate"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.7.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("top.zibin:Luban:1.1.8")
}

/**
 *  JitPack 发布配置
 * - JitPack 构建时会执行 `publish` 任务
 * - groupId 必须是 com.github.<GitHub用户名>
 * - artifactId 建议和模块名一致（mediamate）
 * - version 要和 GitHub tag 一致
 */
publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
            groupId = "com.github.yeqiu"
            artifactId = "mediamate"
            version = "1.0.0"
        }
    }
}
