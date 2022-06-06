package com.license.workguru_app.profile.data.repository

import com.license.workguru_app.di.RetrofitInstance
import com.license.workguru_app.profile.data.remote.DTO.*
import okhttp3.MultipartBody
import okhttp3.Response

class ProfileRepository {
    suspend fun listUserHistory(access_token:String): List<Any> {
        return RetrofitInstance.apiProfile.listUserHistory(access_token)
    }

    suspend fun listCountries(access_token:String): CountriesResponse {
        return RetrofitInstance.apiProfile.listCountries(access_token)
    }

    suspend fun listStates(access_token:String, country_name:String): StatesResponse {
        return RetrofitInstance.apiProfile.listStates(access_token, country_name)
    }

    suspend fun listCities(access_token:String, state_id:Int): CitiesResponse{
        return RetrofitInstance.apiProfile.listCities(access_token,state_id )
    }

    suspend fun getUserProfile(access_token:String): ProfileResponse{
        return RetrofitInstance.apiProfile.getUserProfile(access_token )
    }

    suspend fun changeProfileData(
        access_token:String,
        city:String?,
        street_address:String?,
        country: String?,
        avatar: MultipartBody.Part?,
        _method: String?,
        state: String?,
        isRemoving: Boolean?,
        phone_number:String?,
        zip:String?
    ): MessageResponse{
        return RetrofitInstance.apiProfile.changeProfileData(
            access_token,

            city,
            street_address,
            country,
            avatar,
            _method,
            state,
            isRemoving,
            phone_number,
            zip
        )
    }

//    suspend fun changeProfileData(
//        access_token:String,
//        city:String?,
//        street_address:String?,
//        country: String?,
//        avatar: String?,
//        _method:String?,
//        state:String?,
//        isRemoving:Boolean?
//    ): MessageResponse{
//        return RetrofitInstance.apiProfile.changeProfileData(access_token, ProfileRequest(city, street_address, country, avatar, _method, state, isRemoving ))
//    }

    suspend fun listColleagues(access_token:String, page:Int): ColleagueResponse{
        return RetrofitInstance.apiProfile.listColleagues(access_token,page )
    }

    suspend fun storeFace(access_token:String, request: StoreFaceRequest): retrofit2.Response<Unit> {
        return RetrofitInstance.apiProfile.storeFace(access_token,request )
    }

    suspend fun updateFace(access_token:String, request: StoreFaceRequest): retrofit2.Response<Unit> {
        return RetrofitInstance.apiProfile.updateFace(access_token,request )
    }

    suspend fun deleteFace(access_token:String, request: StoreFaceRequest): retrofit2.Response<Unit> {
        return RetrofitInstance.apiProfile.deleteFace(access_token )
    }

}