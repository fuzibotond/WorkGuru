package com.license.workguru_app.profile.data.remote

import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.profile.data.remote.DTO.*
import com.license.workguru_app.timetracking.data.remote.DTO.CategoryResponse
import com.license.workguru_app.utils.Constants
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

    @FormUrlEncoded
    @POST(Constants.CHANGE_USER_PROFILE)
    suspend fun changeProfileData(
        @Header("Authorization") token:String,
        @Field("city") city:String?,
        @Field("street_address") street_address:String?,
        @Field("country") country: String?,
        @Field("avatar") avatar: String,
        @Field("_method") _method:String,
        @Field("state") state:String
    ): MessageResponse

}