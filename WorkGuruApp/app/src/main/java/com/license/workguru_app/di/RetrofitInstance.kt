package com.license.workguru_app.di

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext
import com.license.workguru_app.authentification.data.remote.AuthentificationApi
import com.license.workguru_app.timetracking.data.remote.TimeTrackingApi
import com.license.workguru_app.utils.Constants.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import okhttp3.OkHttpClient
import okhttp3.Request


object RetrofitInstance {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
//    var client = OkHttpClient.Builder().addInterceptor { chain ->
//        val newRequest: Request = chain.request().newBuilder()
//            .build()
//        chain.proceed(newRequest)
//    }.build()


    val retrofit = Retrofit.Builder()
//        .client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: AuthentificationApi by lazy{
        retrofit.create(AuthentificationApi :: class.java)
    }
    val apiTimeTracking: TimeTrackingApi by lazy{
        retrofit.create(TimeTrackingApi :: class.java)
    }
}
