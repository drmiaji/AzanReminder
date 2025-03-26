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
        minSdk = 21
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
    // unit testing
    testImplementation(libs.mockito.core)
    testImplementation (libs.junit)
    testImplementation (libs.mockito.inline)
    testImplementation (libs.kotlinx.coroutines.test)
    testImplementation (libs.androidx.core.testing)
    // Instrumented Unit Tests
    androidTestImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation ("com.google.truth:truth:1.0.1")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("org.mockito:mockito-core:2.25.0")
    // Coroutines test library
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
    // LiveData test utilities
    testImplementation ("androidx.arch.core:core-testing:2.1.0")
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
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:2.6.1")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:2.6.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // sdp
    implementation("com.intuit.sdp:sdp-android:1.1.1")


    // Room KTX (for Kotlin Coroutines support)
    implementation ("androidx.room:room-ktx:2.6.1")
    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    //dagger Hilt
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-android-compiler:2.52")
    // fragment Ktx
    implementation ("androidx.fragment:fragment-ktx:1.8.5")
    // navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.4")
    implementation ("androidx.navigation:navigation-ui-ktx:2.8.4")
    //ssp
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    //firebase

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    //OKHttp
    implementation ("com.squareup.okhttp3:logging-interceptor:4.5.0")
    //Cirecle
    implementation ("de.hdodenhof:circleimageview:3.1.0")
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
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
    // lottie
    implementation ("com.airbnb.android:lottie:6.0.0")
    // play service location
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:17.0.0")
    implementation ("org.osmdroid:osmdroid-android:6.1.16")

    // work manager
    implementation("androidx.work:work-runtime-ktx:2.7.1")
}
kapt{
    correctErrorTypes = true

}