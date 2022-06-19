package com.license.workguru_app.di


import com.license.workguru_app.admin.data.remote.AdminApi
import com.license.workguru_app.authentification.data.remote.AuthenticationApi
import com.license.workguru_app.help_request.data.remote.HelpRequestApi
import com.license.workguru_app.profile.data.remote.ProfileApi
import com.license.workguru_app.timetracking.data.remote.TimeTrackingApi
import com.license.workguru_app.utils.Constants.BASE_URL
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient.Builder
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    var okHttpClient: OkHttpClient = Builder()
        .addInterceptor(interceptor)
        .connectTimeout(200, TimeUnit.SECONDS)
        .readTimeout(200, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val API: AuthenticationApi by lazy{
        retrofit.create(AuthenticationApi :: class.java)
    }
    val apiTimeTracking: TimeTrackingApi by lazy{
        retrofit.create(TimeTrackingApi :: class.java)
    }
    val apiProfile: ProfileApi by lazy{
        retrofit.create(ProfileApi :: class.java)
    }
    val apiAdmin: AdminApi by lazy{
        retrofit.create(AdminApi :: class.java)
    }
    val helpRequest: HelpRequestApi by lazy{
        retrofit.create(HelpRequestApi :: class.java)
    }
}

