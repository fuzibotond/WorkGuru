package com.license.workguru_app.profile.data.remote

import com.license.workguru_app.profile.data.remote.DTO.*
import com.license.workguru_app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

import retrofit2.http.POST

import retrofit2.http.Multipart
import retrofit2.http.Body


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
//    @POST(Constants.CHANGE_USER_PROFILE)
//    suspend fun changeProfileData(
//        @Header("Authorization") token:String,
//        @Field("city") city:String?,
//        @Field("street_address") street_address:String?,
//        @Field("country") country: String?,
//        @Field("avatar") avatar: String?,
//        @Field("_method") _method:String,
//        @Field("state") state:String,
//        @Field("delete_avatar") delete:Boolean?
//    ): MessageResponse
    @Multipart
    @PUT(Constants.CHANGE_USER_PROFILE)
    suspend fun changeProfileData(
    @Header("Authorization") token:String,
    @Part("city") city:String?,
    @Part("street_address") street_address:String?,
    @Part("country") country: String?,
    @Part avatar: MultipartBody.Part?,
    @Part("state") state:String?,
    @Part("delete_avatar") delete:Boolean?,
    @Part("phone_number") phone_number:String?,
    @Part("zip") zip:String?
    ): MessageResponse

    @Multipart
    @PUT(Constants.CHANGE_USER_PROFILE)
    suspend fun changeProfileData(
        @Header("Authorization") token:String,
        @Part("city") city:RequestBody?,
        @Part("street_address") street_address:RequestBody?,
        @Part("country") country: RequestBody?,
        @Part avatar: MultipartBody.Part?,
        @Part("state") state:RequestBody?,
        @Part("delete_avatar") delete:RequestBody?,
        @Part("phone_number") phone_number:RequestBody?,
        @Part("zip") zip:RequestBody?
    ): MessageResponse

    @Multipart
    @PUT(Constants.CHANGE_USER_PROFILE)
    suspend fun changeProfileData(
        @Header("Authorization") token:String?,
        @Part("city") city:RequestBody?,
        @Part("street_address") street_address:RequestBody?,
        @Part("country") country: RequestBody?,
        @Part("avatar") avatar:RequestBody,
        @Part("state") state:RequestBody?,
        @Part("delete_avatar") delete:RequestBody?,
        @Part("phone_number") phone_number:RequestBody?,
        @Part("zip") zip:RequestBody?
    ): MessageResponse

//    @POST(Constants.CHANGE_USER_PROFILE)
//    suspend fun changeProfileData(
//        @Header("Authorization") token:String,
//        @Body request: ProfileRequest
//    ): MessageResponse



    @GET(Constants.GET_COLLEAGUES)
    suspend fun listColleagues(@Header("Authorization") token:String, @Query("page") page:Int): ColleagueResponse

    @PUT(Constants.CHANGE_USER_PROFILE)
    suspend fun getPostCreateBodyResponse(
        @Header("Content-Type") accept: String?,
        @Header("Authorization") authorization: String?,
        @Body file: RequestBody?
    ): MessageResponse

    @Multipart
    @POST(Constants.STORE_FACE)
    suspend fun storeFace(@Header("Authorization")token:String,@Part avatar: MultipartBody.Part?, ):Response<Unit>

    @POST(Constants.STORE_FACE)
    suspend fun storeFace(@Header("Authorization")token:String,@Body request:StoreFaceRequest, ):MessageResponse

    @Multipart
    @PUT(Constants.STORE_FACE)
    suspend fun updateFace(@Header("Authorization")token:String, @Part avatar: MultipartBody.Part?, ):Response<Unit>

    @Multipart
    @POST(Constants.STORE_FACE)
    suspend fun updateFace(@Header("Authorization")token:String, @Part avatar: MultipartBody.Part?,@Part("_method") request: RequestBody ):Response<Unit>

    @DELETE(Constants.STORE_FACE)
    suspend fun deleteFace(@Header("Authorization")token:String):Response<Unit>
}