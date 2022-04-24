package com.license.workguru_app.di


import com.license.workguru_app.authentification.data.remote.AuthentificationApi
import com.license.workguru_app.profile.data.remote.DTO.CustomObjAdapter
import com.license.workguru_app.profile.data.remote.DTO.HistoryResponse
import com.license.workguru_app.profile.data.remote.DTO.ProjectHistory
import com.license.workguru_app.profile.data.remote.DTO.TodaysHistory
import com.license.workguru_app.profile.data.remote.ProfileApi
import com.license.workguru_app.timetracking.data.remote.TimeTrackingApi
import com.license.workguru_app.utils.Constants.BASE_URL
import com.squareup.moshi.*
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory



object RetrofitInstance {

    var type = Types.newParameterizedType(MutableList::class.java, HistoryResponse::class.java)
    private val moshi = Moshi.Builder()
        .add(CustomObjAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()
    var jsonAdapter: JsonAdapter<List<HistoryResponse>> = moshi.adapter(type)

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
    val apiProfile: ProfileApi by lazy{
        retrofit.create(ProfileApi :: class.java)
    }
}

