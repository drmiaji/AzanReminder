plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.islamic.prayertimesapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.islamic.prayertimesapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // retrofit
    implementation(libs.rxjava)
    implementation (libs.rxandroid)
    implementation (libs.retrofit)
    implementation (libs.adapter.rxjava3)
    implementation (libs.converter.gson)

    // lottie Animation
    implementation (libs.lottie)
    //Glide Image
    implementation (libs.glide)

    implementation (libs.androidx.room.runtime)
    kapt (libs.androidx.room.compiler)
    implementation( libs.androidx.room.rxjava3) // Room RxJava integration

    implementation (libs.rxjava)
    implementation ( libs.rxandroid)

    implementation(libs.androidx.room.runtime)
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:2.6.1")
    implementation (libs.androidx.cardview)

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:2.6.1")
    //Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // optional - RxJava2 support for Room
    implementation(libs.androidx.room.rxjava2)

    // optional - RxJava3 support for Room
    implementation(libs.androidx.room.rxjava3)

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // sdp
    implementation(libs.sdp.android)


    // Room KTX (for Kotlin Coroutines support)
    implementation (libs.androidx.room.ktx)
    // ViewModel
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData
    implementation (libs.androidx.lifecycle.livedata.ktx)
    //dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    // fragment Ktx
    implementation (libs.androidx.fragment.ktx)
    // navigation
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    //ssp
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    //firebase

    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    implementation (libs.firebase.messaging.ktx)
    implementation ("com.google.firebase:firebase-analytics-ktx")
    //OKHttp
    implementation (libs.logging.interceptor)
    //Cirecle
    implementation (libs.circleimageview)
//    // مكتبة BouncyCastle
//    implementation ("org.bouncycastle:bcprov-jdk15on:1.70")
//
//    // مكتبة Conscrypt
//    implementation ("org.conscrypt:conscrypt-android:2.5.0")
//
//    // مكتبة OpenJSSE
//    implementation ("org.openjsse:openjsse:1.1.0")
//
//    // مكتبة OkHttp التي تعتمد على المكتبات السابقة
//    implementation ("com.squareup.okhttp3:okhttp:4.5.0")

    //gif
    implementation (libs.android.gif.drawable)
    // lottie
    implementation (libs.lottie)
    // play service location
    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)
    implementation (libs.osmdroid.android)

    // work manager
    implementation(libs.androidx.work.runtime.ktx)
}
kapt{
    correctErrorTypes = true

}