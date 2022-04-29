package com.license.workguru_app.profile.data.remote

import com.license.workguru_app.profile.data.remote.DTO.*
import com.license.workguru_app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApi {

    @GET(Constants.GET_USER_HISTORY)
    suspend fun listUserHistory(@Header("Authorization") token:String): List<Any>

    @GET(Constants.GET_COUNTRIES)
    suspend fun listCountries(@Header("Authorization") token:String): CountriesResponse

    @GET(Constants.GET_STATES)
    suspend fun listStates(@Header("Authorization") token:String, @Query("country_name") country_name:String): StatesResponse

    @GET(Constants.GET_CITIES)
    suspend fun listCities(@Header("Authorization") token:String, @Query("state_id") state_id:Int): CitiesResponse

    @GET(Constants.GET_USER_PROFILE)
    suspend fun getUserProfile(@Header("Authorization") token:String): ProfileResponse

//    @FormUrlEncoded
//    @PUT(Constants.CHANGE_USER_PROFILE)
//    suspend fun changeProfileData(
//        @Header("Authorization") token:String,
//        @Field("city") city:String?,
//        @Field("street_address") street_address:String?,
//        @Field("country") country: String?,
//        @Field("avatar") avatar: String,
//        @Field("_method") _method:String,
//        @Field("state") state:String
//    ): MessageResponse
    @Multipart
    @POST(Constants.CHANGE_USER_PROFILE)
    suspend fun changeProfileData(
    @Header("Authorization") token:String,
    @Part("city") city:String?,
    @Part("street_address") street_address:String?,
    @Part("country") country: String?,
    @Part avatar: MultipartBody.Part,
    @Part("_method") _method:String,
    @Part("state") state:String
    ): MessageResponse

//    @PUT(Constants.CHANGE_USER_PROFILE)
//    suspend fun changeProfileData(
//        @Header("Authorization") token:String,
//        @Body request: ProfileRequest
//    ): MessageResponse

}