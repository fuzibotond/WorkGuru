package com.example.workguru_app.di

//import com.example.workguru_app.authentification.data.remote.AuthentificationApi
import com.example.workguru_app.utils.Constants.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

//    val api: AuthentificationApi by lazy{
//        retrofit.create(AuthentificationApi :: class.java)
//    }
}